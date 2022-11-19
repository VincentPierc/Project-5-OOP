import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DudeFull extends Carriable{

    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit);
    }

    public Action createAnimationAction(int repeatCount) {
        Animate animate = new Animate(this, repeatCount);
        return animate;
    }

    public Action createActivityAction(WorldModel world, ImageStore imageStore) {
        Activity activity = new Activity(this, world, imageStore);
        return activity;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), super.getActionPeriod());
        scheduler.scheduleEvent(this, this.createAnimationAction(0), super.getAnimationPeriod());
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(super.getEntityPosition(), House.class);

        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            this.transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), super.getActionPeriod());
        }
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (super.getEntityPosition().adjacent(target.getEntityPosition())) {
            return true;
        } else {
            Point nextPos = super.nextPosition(world, target.getEntityPosition());

            if (!this.getEntityPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }


    public void transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dudeNotFull = new DudeNotFull(super.getEntityID(), super.getEntityPosition(), imageStore.getImageList(Functions.DUDE_KEY), super.getActionPeriod(), super.getAnimationPeriod(), Functions.DUDE_LIMIT, 0);

        world.removeEntity(scheduler, this);

        world.addEntity(dudeNotFull);
        dudeNotFull.scheduleActions(scheduler, world, imageStore);
    }
}
