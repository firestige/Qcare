package xyz.firestige.qcare.server.core.ws.method;

public class ControllerMethodResolver {

    public InvocableHandlerMethod getInvocableHandlerMethod(HandlerMethod method) {
        // Logic to create and return an InvocableHandlerMethod based on the provided HandlerMethod
        return new InvocableHandlerMethod(method);
    }
}
