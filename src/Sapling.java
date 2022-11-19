import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Sapling extends Actionable implements Transform {

    public Sapling(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod, health, healthLimit);
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
        super.setHealth(super.getHealth() + 1);

        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), super.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (super.getHealth() <= 0) {
            Stump stump = new Stump(super.getEntityID(), super.getEntityPosition(), imageStore.getImageList(Functions.STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            return true;
        } else if (super.getHealth() >= super.getHealthLimit()) {
            Tree tree = new Tree(Functions.TREE_KEY + "_" + super.getEntityID(), super.getEntityPosition(), imageStore.getImageList(Functions.TREE_KEY), getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN), getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN),
                    getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), Functions.TREE_HEALTH_MAX);

            world.removeEntity(scheduler, this);
            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);
            return true;
        }

        return false;
    }
    public int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max - min);
    }

    public double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }
}
