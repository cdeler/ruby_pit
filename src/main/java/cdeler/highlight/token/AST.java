package cdeler.highlight.token;

import java.util.List;
import java.util.function.Function;

public class AST<T> {
    private final T node;
    private final List<AST<T>> childes;

    public AST(T node, List<AST<T>> childes) {
        this.node = node;
        this.childes = childes;
    }

    public void walk(Function<T, Void> functor) {
        functor.apply(node);

        if (childes != null) {
            for (var child : childes) {
                child.walk(functor);
            }
        }
    }
}
