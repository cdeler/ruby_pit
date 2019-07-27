package cdeler.core;

import java.util.Objects;

public class Pair<T> {
    private final T m_first;
    private final T m_second;

    public Pair(T first, T second) {
        m_first = first;
        m_second = second;
    }

    public T first() {
        return m_first;
    }

    public T second() {
        return m_second;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Pair)) {
            return false;
        }
        return Objects.equals(((Pair) other).m_first, this.m_first) && Objects.equals(((Pair) other).m_second,
                this.m_second);
    }

    public int hashCode() {
        return UIUtils.hashCombine(Objects.hashCode(this.m_first), Objects.hashCode(this.m_second));
    }

    public String toString() {
        return "Pair<" + m_first + "," + m_second + ">";
    }
}
