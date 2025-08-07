package xyz.firestige.qcare.server.core.ws.server.mapping;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.util.StringValueResolver;
import xyz.firestige.qcare.server.core.ws.server.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.server.annotation.WsMsgController;
import xyz.firestige.qcare.server.core.ws.server.method.WsMessageInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class WsMessageMappingHandlerMapping extends WsMessageMappingInfoHandlerMapping implements EmbeddedValueResolverAware {

    private StringValueResolver embeddedValueResolver;
    private WsMessageInfo.BuilderConfiguration config = new WsMessageInfo.BuilderConfiguration();

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void afterPropertiesSet() {
        this.config = new WsMessageInfo.BuilderConfiguration();
        this.config.setPatternParser(getPatternParser());
        super.afterPropertiesSet();
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, WsMsgController.class);
    }

    @Override
    protected WsMessageInfo getMappingForMethod(Method method, Class<?> handlerType) {
        WsMessageInfo info = createMessageInfo(method);
        if (Objects.nonNull(info)) {
            WsMessageInfo typeInfo = createMessageInfo(handlerType);
            if (Objects.nonNull(typeInfo)) {
                info = typeInfo.combine(info);
            }
            if (info.getPatternCondition().isEmptyRouteMapping()) {
                info = info.mutate().route("", "/").option(this.config).build();
            }
        }
        return info;
    }

    private WsMessageInfo createMessageInfo(AnnotatedElement element) {
        WsMessageInfo info = null;
        List<AnnotationDescriptor> descriptors = getAnnotationDescriptors(element);
        List<AnnotationDescriptor> msgMappings = descriptors.stream().filter(desc -> desc.annotation instanceof RouteMapping).toList();
        if (!msgMappings.isEmpty()) {
            info = createMessageInfo((RouteMapping) msgMappings.get(0).annotation);
        }
        return info;
    }

    protected WsMessageInfo createMessageInfo(RouteMapping mapping) {
        return WsMessageInfo
                .routes(resolveEmbeddedValuesInPatterns(mapping.route()))
                .mappingName(mapping.name())
                .option(this.config)
                .build();
    }

    protected String[] resolveEmbeddedValuesInPatterns(String... patterns) {
        if (Objects.isNull(this.embeddedValueResolver)) {
            return patterns;
        }
        String[] resolvedPatterns = new String[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
        }
        return resolvedPatterns;
    }



    private static List<AnnotationDescriptor> getAnnotationDescriptors(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
                .stream()
                .filter(MergedAnnotationPredicates.typeIn(RouteMapping.class))
                .filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex))
                .map(AnnotationDescriptor::new)
                .distinct()
                .toList();
    }

    private static class AnnotationDescriptor {
        private final Annotation annotation;
        private final MergedAnnotation<?> root;

        AnnotationDescriptor(MergedAnnotation<?> mergedAnnotation) {
            this.annotation = mergedAnnotation.synthesize();
            this.root = mergedAnnotation.getRoot();
        }

        @Override
        public int hashCode() {
            return this.annotation.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AnnotationDescriptor other && this.annotation.equals(other.annotation);
        }

        @Override
        public String toString() {
            return this.root.synthesize().toString();
        }
    }
}
