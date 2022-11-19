import processing.core.PImage;

import java.util.List;

public abstract class Animated extends Entity{
    private double animationPeriod;

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    public Animated(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public abstract Action createAnimationAction(int repeatCount);

    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

}
