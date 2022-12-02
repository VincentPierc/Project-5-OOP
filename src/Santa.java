import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Santa extends Movable {

    public Santa(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod, health, healthLimit);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), super.getActionPeriod());
        scheduler.scheduleEvent(this, this.createAnimationAction(0), super.getAnimationPeriod());
    }

    public Action createAnimationAction(int repeatCount) {
        Animate animate = new Animate(this, repeatCount);
        return animate;
    }

    public Action createActivityAction(WorldModel world, ImageStore imageStore) {
        Activity activity = new Activity(this, world, imageStore);
        return activity;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> santaTarget = world.findNearest(super.getEntityPosition(), House.class);
        if (santaTarget.isPresent()) {
            Point tgtPos = santaTarget.get().getEntityPosition();
            Optional<Entity> house = world.getOccupant(tgtPos);
            Entity entity = house.get();
            if (this.moveTo(world, santaTarget.get(), scheduler)) {
                scheduler.unscheduleAllEvents(entity);
                world.removeEntityAt(tgtPos);
                Present present = new Present(Functions.SANTA_KEY + "_" + santaTarget.get().getEntityID(), tgtPos, imageStore.getImageList(Functions.PRESENT_KEY),
                        Functions.PRESENT_ANIMATION_PERIOD, Functions.PRESENT_ACTION_PERIOD, Functions.PRESENT_PRESENT_HEALTH, 1);
                world.tryAddEntity(present);
                present.scheduleActions(scheduler, world, imageStore);


//                Optional<Entity> entityOptional = world.getOccupant(pressed);
//                if (entityOptional.isPresent()) {
//                    Entity entity = entityOptional.get();
//                    scheduler.unscheduleAllEvents(entity);
//                    world.removeEntityAt(pressed);
//
//                Santa santa = new Santa(Functions.SANTA_KEY, pressed, imageStore.getImageList(Functions.SANTA_KEY),
//                        Functions.SANTA_ANIMATION_PERIOD, Functions.SANTA_ACTION_PERIOD, 10, 10);
//                world.tryAddEntity(santa);
//                santa.scheduleActions(scheduler, world, imageStore);


                }


        }
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (super.getEntityPosition().adjacent(target.getEntityPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getEntityPosition());

            if (!super.getEntityPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    /**
     * SingleStepPathingStrategy
     * @param world
     * @param destPos
     * @return
     */
    public Point nextPosition(WorldModel world, Point destPos) {
        List<Point> path = new ArrayList<Point>();
        //SingleStepPathingStrategy singleStep = new SingleStepPathingStrategy();
        AStarPathingStrategy strategy = new AStarPathingStrategy();
        Point nextPos;
        //path = singleStep.computePath(this.getEntityPosition(), destPos,
        path = strategy.computePath(this.getEntityPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p), //can pass through
                (p1, p2) -> p1.adjacent(p2),  //withinReach
                PathingStrategy.CARDINAL_NEIGHBORS);

        if(path.size() == 0) {
            //System.out.println("No path");
            nextPos = this.getEntityPosition();
            return nextPos;
        } else {
            nextPos = path.get(0);
            if(world.isOccupied(nextPos)) {
                return this.getEntityPosition();
            }
            return nextPos;
        }
    }
}
