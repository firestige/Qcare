package xyz.firestige.qcare.server.core.agent.event;

import xyz.firestige.qcare.server.core.agent.model.AgentInfo;
import xyz.firestige.qcare.server.proto.event.ApplicationEvent;

public class AgentEvent extends ApplicationEvent {
    private final AgentInfo agentInfo;
    public AgentEvent(AgentInfo info) {
        this(AgentEvent.class.getSimpleName(), info);
    }
    protected AgentEvent(String name, AgentInfo info) {
        super(name);
        this.agentInfo = info;
    }

    @Override
    public AgentInfo getPayload() {
        return agentInfo;
    }
}
