package xyz.firestige.qcare.server.core.ws.server.condition;

import org.springframework.http.server.PathContainer;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class RoutePatternCondition extends AbstractWsMessageCondition<RoutePatternCondition> {
    private static final SortedSet<PathPattern> EMPTY_ROUTE_PATTERN =
            new TreeSet<>(Collections.singleton(PathPatternParser.defaultInstance.parse("")));
    private static final Set<String> EMPTY_ROUTE = Collections.singleton("");

    private final SortedSet<PathPattern> patterns;

    public RoutePatternCondition(PathPattern... patterns) {
        this(ObjectUtils.isEmpty(patterns) ? Collections.emptyList() : Arrays.asList(patterns));
    }

    public RoutePatternCondition(Collection<PathPattern> patterns) {
        this.patterns = patterns.isEmpty() ? EMPTY_ROUTE_PATTERN : new TreeSet<>(patterns);
    }

    private RoutePatternCondition(SortedSet<PathPattern> patterns) {
        this.patterns = patterns;
    }

    public SortedSet<PathPattern> getPatterns() {
        return patterns;
    }

    @Override
    protected Collection<PathPattern> getContent() {
        return patterns;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    public boolean isEmptyRouteMapping() {
        return this.patterns == EMPTY_ROUTE_PATTERN;
    }

    public Set<String> getDirectRoutes() {
        if (isEmptyRouteMapping()) {
            return EMPTY_ROUTE;
        }
        Set<String> result = Collections.emptySet();
        for (PathPattern pattern : patterns) {
            if (!pattern.hasPatternSyntax()) {
                result = result.isEmpty() ? new HashSet<>(1) : result;
                result.add(pattern.getPatternString());
            }
        }
        return result;
    }

    @Override
    public RoutePatternCondition combine(RoutePatternCondition other) {
        if (isEmptyRouteMapping() && other.isEmptyRouteMapping()) {
            return new RoutePatternCondition(EMPTY_ROUTE_PATTERN);
        } else if (other.isEmptyRouteMapping()) {
            return this;
        } else if (isEmptyRouteMapping()) {
            return other;
        } else {
            SortedSet<PathPattern> combined = new TreeSet<>();
            for (PathPattern pattern : patterns) {
                for (PathPattern pattern2 : other.patterns) {
                    combined.add(pattern.combine(pattern2));
                }
            }
            return new RoutePatternCondition(combined);
        }
    }

    @Override
    public RoutePatternCondition getMatchingCondition(WsExchange exchange) {
        SortedSet<PathPattern> matches = getMatchingPatterns(exchange);
        return Objects.nonNull(matches) ? new RoutePatternCondition(matches) : null;
    }

    private SortedSet<PathPattern> getMatchingPatterns(WsExchange exchange) {
        PathContainer lookupPath = PathContainer.parsePath(exchange.message().getRoute());
        TreeSet<PathPattern> result = null;
        for (PathPattern pattern : patterns) {
            if (pattern.matches(lookupPath)) {
                result = Objects.isNull(result) ? new TreeSet<>() : result;
                result.add(pattern);
            }
        }
        return result;
    }

    @Override
    public int compareTo(RoutePatternCondition other, WsExchange exchange) {
        Iterator<PathPattern> iterator = other.patterns.iterator();
        Iterator<PathPattern> otherIterator = other.patterns.iterator();
        while (iterator.hasNext() && otherIterator.hasNext()) {
            int result = PathPattern.SPECIFICITY_COMPARATOR.compare(iterator.next(), otherIterator.next());
            if (result != 0) {
                return result;
            }
        }
        if (iterator.hasNext()) {
            return -1;
        } else if (otherIterator.hasNext()) {
            return 1;
        } else  {
            return 0;
        }
    }
}
