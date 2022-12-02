import processing.core.PImage;

import java.util.List;

public class Present extends Tree {

    public Present(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
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

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (super.getHealth() <= 0) {
            Stump stump = new Stump(super.getEntityID(), super.getEntityPosition(), imageStore.getImageList(Functions.STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            System.out.println(this.getEntityPosition());
            //world.setBackgroundSnow(this.getEntityPosition(), imageStore);
            return true;
//            if (super.getHealth() <= 0) {
//                House house = new House(Functions.HOUSE_KEY, this.getEntityPosition(), imageStore.getImageList(Functions.HOUSE_KEY));
//                world.removeEntity(scheduler, this);
//                world.addEntity(house);
//                world.setBackgroundSnow(this.getEntityPosition(), imageStore);
//                return true;
//            }
        }
        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), super.getActionPeriod());
        }
    }
}

