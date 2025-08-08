package xyz.firestige.qcare.server.core.ws.server.method;

import java.util.List;

public class ControllerMethodResolver {

    private List<HandlerMethodArgumentResolver> argumentResolvers;

    public ControllerMethodResolver(List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public InvocableHandlerMethod getInvocableHandlerMethod(HandlerMethod method) {
        // TODO 需要补充？
        InvocableHandlerMethod invocableHandlerMethod =  new InvocableHandlerMethod(method);
        invocableHandlerMethod.setArgumentResolvers(argumentResolvers);
        return invocableHandlerMethod;
    }
}
