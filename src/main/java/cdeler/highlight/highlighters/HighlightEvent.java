package cdeler.highlight.highlighters;

import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.jetbrains.annotations.NotNull;

import static cdeler.highlight.highlighters.HighlightEventPriority.REDRAW_VISIBLE_PART;

class HighlightEvent {
    @NotNull
    private final HighlightEventPriority priority;

    @NotNull
    private final Future<?> highlightEvent;

    HighlightEvent(@NotNull HighlightEventPriority priority, @NotNull Future<?> highlightEvent) {
        this.priority = priority;
        this.highlightEvent = highlightEvent;
    }

    @NotNull
    public HighlightEventPriority getPriority() {
        return priority;
    }

    public synchronized boolean preemptiveCancelBy(@NotNull HighlightEventPriority otherEventPriority) {
        boolean result;
        switch (priority) {
            case REDRAW_ALL:
                result = false;
                break;
            case REDRAW_VISIBLE_PART:
                result = highlightEvent.cancel(true);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + priority);
        }

        return result;
    }

    public boolean isDone() {
        return highlightEvent.isDone();
    }

    public boolean isCanceled() {
        return highlightEvent.isCancelled();
    }

    private static HighlightEvent DUMMY_EVENT =
            new HighlightEvent(REDRAW_VISIBLE_PART, ConcurrentUtils.constantFuture(true));

    public static HighlightEvent emptyEvent() {
        return DUMMY_EVENT;
    }

    public boolean isInProgress() {
        return !highlightEvent.isDone() && !highlightEvent.isCancelled();
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + priority + ")";
    }
}
