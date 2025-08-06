package xyz.firestige.qcare.server.core.agent.event;

import xyz.firestige.qcare.server.core.agent.model.AgentInfo;

public class AgentJoinEvent extends AgentEvent {
    public AgentJoinEvent(AgentInfo info) {
        super(AgentJoinEvent.class.getSimpleName(), info);
    }
}
