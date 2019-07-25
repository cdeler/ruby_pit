package cdeler.core;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface EventProducer {
    Map<UIEventType, Function<List<UIEvent>, Void>> getEventList();
}
