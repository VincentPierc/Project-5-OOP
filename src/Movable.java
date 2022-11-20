import processing.core.PImage;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Movable extends Actionable {

    public Movable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod, health, healthLimit);
    }

    /** Constructor for DudeFull */
    public Movable(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public abstract Action createActivityAction(WorldModel world, ImageStore imageStore);
    public abstract Action createAnimationAction(int repeatCount);
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    public abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
    public abstract Point nextPosition(WorldModel world, Point destPos);


}
