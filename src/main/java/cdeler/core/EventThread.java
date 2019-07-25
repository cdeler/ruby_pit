package cdeler.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EventThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventThread.class);
    private static final int HANDLED_EVENTS_THRESHOLD = 50;
    private static final int DELAY_SIZE_MS = 100;

    private final BlockingQueue<UIEvent> eventQueue;
    private final Map<UIEventType, Function<List<UIEvent>, Void>> consumers;

    public EventThread(Map<UIEventType, Function<List<UIEvent>, Void>> events) {
        eventQueue = new LinkedBlockingDeque<>();
        consumers = new ConcurrentHashMap<>(events);
    }

    public EventThread() {
        eventQueue = new LinkedBlockingDeque<>();
        consumers = new ConcurrentHashMap<>();
    }

    public void fire(UIEvent event) {
        eventQueue.add(event);
    }

    public void addConsumers(Map<UIEventType, Function<List<UIEvent>, Void>> events) {
        consumers.putAll(events);
    }

    @Override
    public void run() {
        while (true) {
            try {
                try {
                    var headEvent = eventQueue.poll(DELAY_SIZE_MS, TimeUnit.MILLISECONDS);

                    if (headEvent != null) {
                        var processedEvents = new ArrayList<UIEvent>();
                        processedEvents.add(headEvent);

                        while (!eventQueue.isEmpty() && processedEvents.size() < HANDLED_EVENTS_THRESHOLD) {
                            headEvent = eventQueue.take();
                            processedEvents.add(headEvent);
                        }

                        processedEvents.stream().collect(Collectors.groupingBy(UIEvent::getEventType))
                                .forEach((uiEventType, uiEvents) -> {
                                    if (consumers.containsKey(uiEventType)) {
                                        consumers.get(uiEventType).apply(uiEvents);
                                    }
                                });
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }

                Thread.sleep(DELAY_SIZE_MS);
            } catch (InterruptedException e) {
                LOGGER.error("Something went wrong", e);
                // ignore it...
                // TODO add log may be?
            }
        }
    }
}
