package xyz.firestige.qcare.server.core.ws.server.method;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.*;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

public class HandlerMethod extends AnnotatedMethod {

    private final Object bean;
    private final BeanFactory factory;
    private final Class<?> beanType;
    private final boolean validateArguments;
    private final boolean validateReturnType;

    public HandlerMethod(String beanName, BeanFactory factory, Method method) {
        super(method);
        this.bean = beanName;
        this.factory = factory;
        Class<?> beanType = Optional.ofNullable(factory.getType(beanName))
                .orElseThrow(() -> new IllegalArgumentException("Cannot resolve bean type for bean with name '" + beanName + "'"));
        this.beanType = ClassUtils.getUserClass(beanType);
        this.validateArguments = false;
        this.validateReturnType = false;
    }

    public HandlerMethod(Object bean, Method method) {
        super(method);
        this.bean = bean;
        this.factory = null;
        this.beanType = bean.getClass();
        this.validateArguments = false;
        this.validateReturnType = false;
    }

    protected HandlerMethod(HandlerMethod handlerMethod) {
        this(handlerMethod, null, false);
    }

    protected HandlerMethod(HandlerMethod handlerMethod, @Nullable Object handler, boolean initValidateFlags) {
        super(handlerMethod);
        this.bean = Optional.ofNullable(handler).orElse(handlerMethod.getBean());
        this.factory = handlerMethod.factory;
        this.beanType = handlerMethod.getBeanType();
        this.validateArguments = initValidateFlags
                ? MethodValidationInitializer.checkArguments(this.beanType, getMethodParameters())
                : handlerMethod.validateArguments;
        this.validateReturnType = initValidateFlags
                ? MethodValidationInitializer.checkReturnValue(this.beanType, getBridgedMethod())
                :  handlerMethod.validateReturnType;
    }

    public Object getBean() {
        return this.bean;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    public boolean isValidateArguments() {
        return this.validateArguments;
    }
    public boolean isValidateReturnType() {
        return this.validateReturnType;
    }

    /**
     * 创建可调用的处理器方法
     */
    public InvocableHandlerMethod createInvocableHandlerMethod() {
        return new InvocableHandlerMethod(this);
    }

    public HandlerMethod createWithValidateFlags() {
        return new HandlerMethod(this, null, true);
    }

    public HandlerMethod createWithResolvedBean() {
        if (!(this.bean instanceof String beanName)) {
            return this;
        }

        Objects.requireNonNull(this.factory, "cannot resolve bean type for bean with name '" + beanName + "' without factory");
        Object handler = this.factory.getBean(beanName);
        Objects.requireNonNull(handler, "no handler instance with name:'" + beanName + "'");
        return new HandlerMethod(this, handler, false);
    }

    @Override
    public String toString() {
        return getBeanType().getSimpleName() + "#" + getMethod().getName();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof HandlerMethod otherMethod
                && this.bean.equals(otherMethod.bean)
                && this.getMethod().equals(otherMethod.getMethod());
    }

    @Override
    public int hashCode() {
        return (this.bean.hashCode() * 31 + this.getMethod().hashCode());
    }

    private static class MethodValidationInitializer {
        private static final boolean BEAN_VALIDATION_PRESENT =
                ClassUtils.isPresent("jakarta.validation.Validator", org.springframework.web.method.HandlerMethod.class.getClassLoader());

        private static final Predicate<MergedAnnotation<? extends Annotation>> CONSTRAINT_PREDICATE =
                MergedAnnotationPredicates.typeIn("jakarta.validation.Constraint");

        private static final Predicate<MergedAnnotation<? extends Annotation>> VALID_PREDICATE =
                MergedAnnotationPredicates.typeIn("jakarta.validation.Valid");

        public static boolean checkArguments(Class<?> beanType, MethodParameter[] parameters) {
            if (BEAN_VALIDATION_PRESENT && AnnotationUtils.findAnnotation(beanType, Validated.class) == null) {
                for (MethodParameter param : parameters) {
                    MergedAnnotations merged = MergedAnnotations.from(param.getParameterAnnotations());
                    if (merged.stream().anyMatch(CONSTRAINT_PREDICATE)) {
                        return true;
                    }
                    Class<?> type = param.getParameterType();
                    if (merged.stream().anyMatch(VALID_PREDICATE) && isIndexOrKeyBasedContainer(type)) {
                        return true;
                    }
                    merged = MergedAnnotations.from(getContainerElementAnnotations(param));
                    if (merged.stream().anyMatch(CONSTRAINT_PREDICATE.or(VALID_PREDICATE))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean checkReturnValue(Class<?> beanType, Method method) {
            if (BEAN_VALIDATION_PRESENT && AnnotationUtils.findAnnotation(beanType, Validated.class) == null) {
                MergedAnnotations merged = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                return merged.stream().anyMatch(CONSTRAINT_PREDICATE.or(VALID_PREDICATE));
            }
            return false;
        }

        private static boolean isIndexOrKeyBasedContainer(Class<?> type) {

            // Index or key-based containers only, or MethodValidationAdapter cannot access
            // the element given what is exposed in ConstraintViolation.

            return (List.class.isAssignableFrom(type) || Object[].class.isAssignableFrom(type) ||
                    Map.class.isAssignableFrom(type));
        }

        /**
         * There may be constraints on elements of a container (list, map).
         */
        private static Annotation[] getContainerElementAnnotations(MethodParameter param) {
            List<Annotation> result = null;
            int i = param.getParameterIndex();
            Method method = param.getMethod();
            if (method != null && method.getAnnotatedParameterTypes()[i] instanceof AnnotatedParameterizedType apt) {
                for (AnnotatedType type : apt.getAnnotatedActualTypeArguments()) {
                    for (Annotation annot : type.getAnnotations()) {
                        result = (result != null ? result : new ArrayList<>());
                        result.add(annot);
                    }
                }
            }
            result = (result != null ? result : Collections.emptyList());
            return result.toArray(new Annotation[0]);
        }
    }
}
