package xyz.firestige.qcare.agent.config;

import io.micronaut.serde.annotation.SerdeImport;
import xyz.firestige.qcare.protocol.api.agent.http.AgentRegisterRequest;

@SerdeImport(AgentRegisterRequest.class)
public class SerdeImportsConfig {
}
