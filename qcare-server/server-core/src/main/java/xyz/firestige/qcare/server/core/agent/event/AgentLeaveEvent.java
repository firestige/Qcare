package xyz.firestige.qcare.server.core.agent.event;

import xyz.firestige.qcare.server.core.agent.model.AgentInfo;

public class AgentLeaveEvent extends AgentEvent {
    public AgentLeaveEvent(AgentInfo info) {
        super(AgentLeaveEvent.class.getSimpleName(), info);
    }
}
