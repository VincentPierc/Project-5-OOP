import processing.core.PImage;

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


    public Point nextPosition(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - super.getEntityPosition().x);
        Point newPos = new Point(super.getEntityPosition().x + horiz, super.getEntityPosition().y);

        if (horiz == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
            int vert = Integer.signum(destPos.y - super.getEntityPosition().y);
            newPos = new Point(super.getEntityPosition().x, super.getEntityPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
                newPos = super.getEntityPosition();
            }
        }

        return newPos;
    }

    public abstract Action createActivityAction(WorldModel world, ImageStore imageStore);
    public abstract Action createAnimationAction(int repeatCount);
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    public abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
}
