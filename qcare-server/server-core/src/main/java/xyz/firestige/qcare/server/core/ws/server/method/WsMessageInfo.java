package xyz.firestige.qcare.server.core.ws.server.method;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.condition.WsMessageCondition;
import xyz.firestige.qcare.server.core.ws.server.condition.RoutePatternCondition;

import java.util.*;

public final class WsMessageInfo implements WsMessageCondition<WsMessageInfo> {
    private static final RoutePatternCondition EMPTY_PATTERNS = new RoutePatternCondition();

    private final String name;
    private final RoutePatternCondition patternCondition;
    // 以后扩展其他condition
    private final int hashCode;
    private BuilderConfiguration option;

    private WsMessageInfo(String name, RoutePatternCondition patternCondition, BuilderConfiguration option) {
        this.name = StringUtils.hasText(name) ? name : null;
        this.patternCondition = Objects.nonNull(patternCondition) ? patternCondition : EMPTY_PATTERNS;
        this.option = option;
        this.hashCode = calculateHashCode(patternCondition);
    }

    public String getName() {
        return name;
    }

    public RoutePatternCondition getPatternCondition() {
        return patternCondition;
    }

    public Set<String> getDirectRoutes() {
        return this.patternCondition.getDirectRoutes();
    }

    @Override
    public WsMessageInfo combine(WsMessageInfo other) {
        String name = combineNames(other);
        RoutePatternCondition patterns = this.patternCondition.combine(other.getPatternCondition());
        return new WsMessageInfo(name, patterns, this.option);
    }

    private String combineNames(WsMessageInfo other) {
        if (StringUtils.hasText(this.name) && StringUtils.hasText(other.getName())) {
            return this.name + "#" + other.getName();
        } else if (StringUtils.hasText(this.name)) {
            return this.name;
        } else {
            return other.getName();
        }
    }

    @Override
    public WsMessageInfo getMatchingCondition(WsExchange exchange) {
        RoutePatternCondition patterns = this.patternCondition.getMatchingCondition(exchange);
        if (Objects.isNull(patterns)) {
            return null;
        }
        return new WsMessageInfo(this.name, patterns, this.option);
    }

    @Override
    public int compareTo(WsMessageInfo other, WsExchange exchange) {
        int result = this.patternCondition.compareTo(other.getPatternCondition(), exchange);
        if (result != 0) {
            return result;
        }
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof WsMessageInfo that
                        && this.patternCondition.equals(that.patternCondition));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    // 计算hashCode，扩展Condition之后这里也要加入对应的condition
    private static int calculateHashCode(RoutePatternCondition pattern) {
        return Objects.hash(pattern);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (!this.patternCondition.isEmpty()) {
            Set<PathPattern> patterns = this.patternCondition.getPatterns();
            sb.append(' ').append(patterns.size() == 1 ? patterns.iterator().next() : patterns);
        }
        sb.append('}');
        return sb.toString();
    }

    public Builder mutate() {
        return new MutateBuilder(this);
    }

    public static Builder routes(String... routes) {
        return new DefaultBuilder(routes);
    }

    public interface Builder {
        Builder route(String... route);
        // 以后可以添加其他和Message或者session相关的属性
        Builder mappingName(String name);
        Builder option(BuilderConfiguration option);
        WsMessageInfo build();
    }

    private static class DefaultBuilder implements Builder {
        private String[] routes;
        private String mappingName;
        private BuilderConfiguration option = new BuilderConfiguration();

        public DefaultBuilder(String... routes) {
            this.routes = routes;
        }

        @Override
        public Builder route(String... routes) {
            this.routes = routes;
            return this;
        }

        @Override
        public Builder mappingName(String name) {
            this.mappingName = name;
            return this;
        }

        @Override
        public Builder option(BuilderConfiguration option) {
            this.option = option;
            return this;
        }

        @Override
        public WsMessageInfo build() {
            PathPatternParser parser = Objects.nonNull(option.getPatternParser())
                    ? this.option.getPatternParser()
                    : PathPatternParser.defaultInstance;

            return new WsMessageInfo(this.mappingName,
                    isEmpty(routes) ? null : new RoutePatternCondition(parse(routes, parser)),
                    this.option);
        }

        static List<PathPattern> parse(String[] patterns, PathPatternParser parser) {
            if (isEmpty(patterns)) {
                return Collections.emptyList();
            }
            List<PathPattern> result = new ArrayList<>(patterns.length);
            for (String pattern : patterns) {
                pattern = parser.initFullPathPattern(pattern);
                result.add(parser.parse(pattern));
            }
            return result;
        }

        static boolean isEmpty(String[] patterns) {
            if (!ObjectUtils.isEmpty(patterns)) {
                for (String pattern : patterns) {
                    if (StringUtils.hasText(pattern)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static class MutateBuilder implements Builder {
        private String name;
        private RoutePatternCondition patternCondition;
        private BuilderConfiguration option;

        public MutateBuilder(WsMessageInfo original) {
            this.name = original.name;
            this.patternCondition = original.patternCondition;
            this.option = original.option;
        }

        @Override
        public Builder route(String... routes) {
            PathPatternParser parser = Objects.nonNull(option.getPatternParser())
                    ? option.getPatternParser()
                    : PathPatternParser.defaultInstance;
            this.patternCondition = DefaultBuilder.isEmpty(routes)
                    ? null
                    : new RoutePatternCondition(DefaultBuilder.parse(routes, parser));
            return this;
        }

        @Override
        public Builder mappingName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder option(BuilderConfiguration option) {
            this.option = option;
            return this;
        }

        @Override
        public WsMessageInfo build() {
            return new WsMessageInfo(this.name, this.patternCondition, this.option);
        }
    }

    public static class BuilderConfiguration {
        private PathPatternParser patternParser;

        public PathPatternParser getPatternParser() {
            return patternParser;
        }

        public void setPatternParser(PathPatternParser patternParser) {
            this.patternParser = patternParser;
        }
    }
}
