import processing.core.PImage;

import java.util.List;

public abstract class Actionable extends Animated {

    private double actionPeriod;
    private int health;
    private int healthLimit;

    public double getActionPeriod() {
        return this.actionPeriod;
    }

    public int getHealth() {
        return this.health;
    }
    public int getHealthLimit() {
        return this.healthLimit;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public Actionable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    /** Constructor for DudeFull */
    public Actionable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public abstract Action createActivityAction(WorldModel world, ImageStore imageStore);
    public abstract Action createAnimationAction(int repeatCount);
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);


}
