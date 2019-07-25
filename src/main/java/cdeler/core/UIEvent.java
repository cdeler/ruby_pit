package cdeler.core;

public class UIEvent {
    private final UIEventType eventType;

    public UIEvent(UIEventType eventType) {
        this.eventType = eventType;
    }

    public UIEventType getEventType() {
        return eventType;
    }
}
