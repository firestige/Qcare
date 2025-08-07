package xyz.firestige.qcare.server.core.ws.server.mapping;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.HandlerMapping;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class AbstractHandlerMethodMapping<T> extends ApplicationObjectSupport implements HandlerMapping, InitializingBean, BeanNameAware, Ordered {

    private final MappingRegistry registry = new MappingRegistry();
    private final PathPatternParser patternParser = new PathPatternParser();
    private String beanName;
    private int order;

    public Map<T, HandlerMethod> getHandlerMethods() {
        this.registry.lock.readLock().lock();
        try {
            return this.registry.getRegistrations().entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().getHandlerMethod()));
        } finally {
            this.registry.lock.readLock().unlock();
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void setUseCaseSensitiveMatch(boolean useCaseSensitiveMatch) {
        this.patternParser.setCaseSensitive(useCaseSensitiveMatch);
    }

    public PathPatternParser getPatternParser() {
        return this.patternParser;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    MappingRegistry getRegistry() {
        return this.registry;
    }

    public void registerMapping(T mapping, Object handler, Method method) {
        this.registry.register(mapping, handler, method);
    }

    public void unregisterMapping(T mapping) {
        this.registry.unregister(mapping);
    }

    @Override
    public void afterPropertiesSet(){
        initHandlerMethods();
    }

    protected void initHandlerMethods() {
        String[] beanNames = obtainApplicationContext().getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            Class<?> beanType = obtainApplicationContext().getType(beanName);
            if (Objects.nonNull(beanType) && isHandler(beanType)) {
                detectHandlerMethods(beanName);
            }
        }

        handlerMethodsInitialized(getHandlerMethods());
    }

    protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = handler instanceof String beanName
                ? obtainApplicationContext().getType(beanName)
                : handler.getClass();

        if (Objects.nonNull(handlerType)) {
            final Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType, (MethodIntrospector.MetadataLookup<T>) method -> getMappingForMethod(method, userType));
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }

    private String formatMappings(Class<?> userType, Map<Method, T> methods) {
        return "";
    }

    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        registry.register(mapping, handler, method);
    }

    protected HandlerMethod createHandlerMethod(Object handler, Method method) {
        if (handler instanceof String beanName) {
            return new HandlerMethod(beanName, obtainApplicationContext().getAutowireCapableBeanFactory(), method);
        }
        return new HandlerMethod(handler, method);
    }

    protected void handlerMethodsInitialized(Map<T, HandlerMethod> handlerMethods) {}

    @Override
    public Mono<Object> getHandler(WsExchange exchange) {
        this.registry.acquireReadLock();
        try {
            HandlerMethod handlerMethod;
            try {
                handlerMethod = lookupHandlerMethod(exchange);
            } catch (Exception e) {
                return Mono.error(e);
            }
            if (Objects.nonNull(handlerMethod)) {
                handlerMethod = handlerMethod.createWithResolvedBean();
            }
            return Mono.justOrEmpty(handlerMethod);
        } finally {
            this.registry.releaseReadLock();
        }
    }

    protected HandlerMethod lookupHandlerMethod(WsExchange exchange) {
        List<Match> matches = new ArrayList<>();
        List<T> directPathMatches = this.registry.getMappingsByDirectPath(exchange);
        if (Objects.nonNull(directPathMatches)) {
            addMatchingMappings(directPathMatches, matches, exchange);
        }
        if (matches.isEmpty()) {
            addMatchingMappings(this.registry.getRegistrations().keySet(), matches, exchange);
        }
        if (!matches.isEmpty()) {
            Comparator<Match> comparator = new MatchComparator(getMappingComparator(exchange));
            matches.sort(comparator);
            Match bestMatch = matches.getFirst();
            if (matches.size() > 1) {
                Match secondBestMatch = matches.get(1);
                if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                    Method m1 = bestMatch.getHandlerMethod().getMethod();
                    Method m2 = secondBestMatch.getHandlerMethod().getMethod();
                    String path = exchange.session().getHandshakeInfo().getUri().getPath();
                    String route = exchange.message().getRoute();
                    throw new IllegalStateException("Ambiguous handler method found, path: " + path + ", route: " + route +"': {" + m1 + ", " + m2 + "}");
                }
            }
            handleMatch(bestMatch.mapping, bestMatch.getHandlerMethod(), exchange);
            return bestMatch.getHandlerMethod();
        } else {
            return handleNoMatch(this.registry.getRegistrations().keySet(), exchange);
        }
    }

    private void addMatchingMappings(Collection<T> mappings, List<Match> matches, WsExchange exchange) {
        for (T mapping : mappings) {
            T match = getMatchingMapping(mapping, exchange);
            if (Objects.nonNull(match)) {
                matches.add(new Match(match, this.registry.getRegistrations().get(mapping)));
            }
        }
    }

    protected void handleMatch(T match, HandlerMethod handlerMethod, WsExchange exchange) {}

    protected HandlerMethod handleNoMatch(Collection<T> mappings, WsExchange exchange) {
        return null;
    }

    protected abstract boolean isHandler(Class<?> beanType);

    protected abstract T getMappingForMethod(Method method, Class<?> handlerType);

    protected Set<String> getDirectRoutes(T mapping) {
        return Collections.emptySet();
    }

    protected abstract T getMatchingMapping(T mapping, WsExchange exchange);

    protected abstract Comparator<T> getMappingComparator(WsExchange exchange);

    class MappingRegistry {
        private final Map<T, MappingRegistration<T>> registry = new HashMap<>();
        private final MultiValueMap<String, T> pathLookup = new LinkedMultiValueMap<>();
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public Map<T, MappingRegistration<T>> getRegistrations() {
            return registry;
        }

        public List<T> getMappingsByDirectPath(WsExchange exchange) {
            String path = exchange.session().getHandshakeInfo().getUri().getPath();
            return pathLookup.get(path);
        }

        public void acquireReadLock() {
            lock.readLock().lock();
        }

        public void releaseReadLock() {
            lock.readLock().unlock();
        }

        public void register(T mapping, Object handler, Method method) {
            lock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = createHandlerMethod(handler, method);
                validateMethodMapping(handlerMethod, mapping);

                handlerMethod = handlerMethod.createWithValidateFlags();

                Set<String> directPaths = AbstractHandlerMethodMapping.this.getDirectRoutes(mapping);
                for (String path : directPaths) {
                    this.pathLookup.add(path, mapping);
                }

                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directPaths));
            } finally {
                lock.writeLock().unlock();
            }
        }

        private void validateMethodMapping(HandlerMethod handlerMethod, T mapping) {
            MappingRegistration<T> registration = registry.get(mapping);
            HandlerMethod existingHandlerMethod = Objects.nonNull(registration) ? registration.getHandlerMethod() : null;
            if (Objects.nonNull(existingHandlerMethod) && !existingHandlerMethod.equals(handlerMethod)) {
                throw new IllegalStateException("Mapping already exists for method: " + mapping);
            }
        }

        public void unregister(T mapping) {
            lock.writeLock().lock();
            try {
                MappingRegistration<T> registration = registry.remove(mapping);
                if (Objects.isNull(registration)) {
                    return;
                }
                for (String path : registration.getDirectPaths()) {
                    List<T> mappings = pathLookup.get(path);
                    if (Objects.nonNull(mappings)) {
                        mappings.remove(registration.getMapping());
                        if (mappings.isEmpty()) {
                            this.pathLookup.remove(path);
                        }
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    static class MappingRegistration<T> {
        private final T mapping;
        private final HandlerMethod handlerMethod;
        private final Set<String> directPaths;
        public MappingRegistration(T mapping, HandlerMethod handlerMethod, Set<String> directPaths) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directPaths = Objects.nonNull(directPaths) ? directPaths : Collections.emptySet();
        }

        public T getMapping() {
            return mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return handlerMethod;
        }

        public Set<String> getDirectPaths() {
            return directPaths;
        }
    }

    private class Match {
        private final T mapping;
        private final MappingRegistration<T> registration;
        public Match(T mapping, MappingRegistration<T> registration) {
            this.mapping = mapping;
            this.registration = registration;
        }

        public HandlerMethod getHandlerMethod() {
            return registration.getHandlerMethod();
        }

        @Override
        public String toString() {
            return this.registration.toString();
        }
    }

    private class MatchComparator implements Comparator<Match> {
        private final Comparator<T> comparator;

        public MatchComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(Match o1, Match o2) {
            return comparator.compare(o1.mapping, o2.mapping);
        }
    }
}
