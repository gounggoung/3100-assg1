package sim;

public class DSEvent {
    public enum EventType {
        JOB, COMPLETE, FAILURE, RECOVERY, NONE
    }

    public EventType eventType;

    DSEvent(EventType eventType) {
        this.eventType = eventType;
    }
}
