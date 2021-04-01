package sim;

/**
 * The superclass of events that may be returned by the "REDY" command.
 */
public class DSEvent {
    public enum EventType {
        JOB, COMPLETE, FAILURE, RECOVERY, NONE
    }

    public EventType eventType;

    DSEvent(EventType eventType) {
        this.eventType = eventType;
    }
}
