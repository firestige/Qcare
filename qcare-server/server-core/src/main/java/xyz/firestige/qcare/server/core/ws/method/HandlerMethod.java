package xyz.firestige.qcare.server.core.ws.method;

import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class HandlerMethod extends AnnotatedMethod {

    private final Object bean;
    private final Class<?> beanType;
    private final MethodParameter[] parameters;
    private final Class<?> returnType;

    public HandlerMethod(Object bean, Method method) {
        super(method);
        this.bean = bean;
        this.beanType = bean.getClass();
        this.parameters = initMethodParameters();
        this.returnType = method.getReturnType();
    }

    /**
     * 初始化方法参数
     */
    private MethodParameter[] initMethodParameters() {
        int count = getMethod().getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            result[i] = new MethodParameter(getMethod(), i);
        }
        return result;
    }

    public Object getBean() {
        return this.bean;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    public MethodParameter getReturnType() {
        return new MethodParameter(getMethod(), -1);
    }

    public Class<?> getReturnClass() {
        return this.returnType;
    }

    /**
     * 检查方法是否为void返回类型
     */
    public boolean isVoid() {
        return Void.TYPE.equals(this.returnType);
    }

    /**
     * 创建可调用的处理器方法
     */
    public InvocableHandlerMethod createInvocableHandlerMethod() {
        return new InvocableHandlerMethod(this);
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
        if (!(other instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod otherMethod = (HandlerMethod) other;
        return (this.bean.equals(otherMethod.bean) && this.getMethod().equals(otherMethod.getMethod()));
    }

    @Override
    public int hashCode() {
        return (this.bean.hashCode() * 31 + this.getMethod().hashCode());
    }
}
