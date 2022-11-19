public class Animate implements Action {

    private int repeatCount;
    private Animated entity;


    public Animate(Animated entity, int repeatCount) {
        this.repeatCount = repeatCount;
        this.entity = entity;
    }

    public void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, this.entity.createAnimationAction(Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
        }
    }
}
