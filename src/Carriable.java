import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public abstract class Carriable extends Movable {

    private int resourceLimit;

    public int getResourceLimit() {
        return this.resourceLimit;
    }

    public Carriable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit, int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod, health, healthLimit);
        this.resourceLimit = resourceLimit;
    }

    /** Constructor for DudeFull */
    public Carriable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
    }


    /**
     * SingleStepPathingStrategy
     * @param world
     * @param destPos
     * @return
     */
    public Point nextPosition(WorldModel world, Point destPos) {
        List<Point> path = new ArrayList<Point>();
        SingleStepPathingStrategy singleStep = new SingleStepPathingStrategy();
        Point nextPos;
        path = singleStep.computePath(this.getEntityPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p), //can pass through
                (p1, p2) -> p1.adjacent(p2),  //withinReach
                PathingStrategy.CARDINAL_NEIGHBORS);

        if(path.size() == 0) {

            System.out.println("No path");
            nextPos = this.getEntityPosition();
            return nextPos;
        }
        nextPos = path.get(0);
        return nextPos;
    }

    public abstract Action createActivityAction(WorldModel world, ImageStore imageStore);
    public abstract Action createAnimationAction(int repeatCount);
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    public abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
}
