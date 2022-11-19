import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Carriable implements Transform {

    private int resourceCount;

    public int getResourceCount() {
        return this.resourceCount;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit);
        this.resourceCount = resourceCount;
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
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        Tree tree = (Tree) target;
        if (super.getEntityPosition().adjacent(target.getEntityPosition())) {
            this.resourceCount++;
            tree.setHealth(tree.getHealth() - 1);
            return true;
        } else {
            Point nextPos = super.nextPosition(world, target.getEntityPosition());

            if (!super.getEntityPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getResourceCount() >= super.getResourceLimit()) {

            DudeFull dudeFull = new DudeFull(super.getEntityID(), super.getEntityPosition(), imageStore.getImageList(Functions.DUDE_KEY), super.getActionPeriod(), super.getAnimationPeriod(), Functions.DUDE_LIMIT);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dudeFull);
            dudeFull.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(super.getEntityPosition(), Tree.class);

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), super.getActionPeriod());
        }
    }
}
