import processing.core.PImage;

import java.util.List;

public class Obstacle extends Animated{
    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images, animationPeriod);
    }

    public Action createAnimationAction(int repeatCount) {
        Animate animate = new Animate(this, repeatCount);
        return animate;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createAnimationAction(0), this.getAnimationPeriod());
    }

}
