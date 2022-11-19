/**
 * An action that can be taken by an entity
 */
public interface Action {


    public abstract void executeAction(EventScheduler scheduler);
     }
