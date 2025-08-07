package xyz.firestige.qcare.server.core.ws.condition;

import org.springframework.lang.Nullable;
import xyz.firestige.qcare.server.core.ws.WsMessageCondition;

import java.util.Collection;
import java.util.StringJoiner;

public abstract class AbstractWsMessageCondition<T extends AbstractWsMessageCondition<T>> implements WsMessageCondition<T> {

    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    protected abstract Collection<?> getContent();

    protected abstract String getToStringInfix();

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractWsMessageCondition<?> another)) {
            return false;
        }
        return getContent().equals(another.getContent());
    }

    @Override
    public int hashCode() {
        return getContent().hashCode();
    }

    @Override
    public String toString() {
        String infix = getToStringInfix();
        StringJoiner joiner = new StringJoiner(infix, "[", "]");
        for (Object expression : getContent()) {
            joiner.add(expression.toString());
        }
        return joiner.toString();
    }
}
