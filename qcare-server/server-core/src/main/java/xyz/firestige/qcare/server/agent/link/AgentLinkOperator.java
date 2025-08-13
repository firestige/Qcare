package xyz.firestige.qcare.server.agent.link;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.vertx.ext.auth.impl.jose.JWT;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.jwt.JsonWebToken;
import xyz.firestige.qcare.server.utils.StringUtils;

import java.util.Map;
import java.util.Objects;


@ApplicationScoped
@ServerEndpoint("/ws/agent/{agentId}")
public class AgentLinkOperator {

    private static final String AGENT_ID = "agentId";

    @Inject
    private AgentLinkManager linkManager;

    @Inject
    private JWTParser parser;

    @Inject
    private JWTAuthContextInfo ctxInfo;

    @OnOpen
    public void onOpen(Session session, @PathParam(AGENT_ID) String agentId) {
        //检查agentId是否有效
        if (!this.ensureAgentIdValid(agentId)) {
            session.close(CloseReasons.BAD_REQUEST);
        }
        String token = StringUtils.parseQueryString(session.getRequestURI().getQuery()).get("token");
        if (!StringUtils.isBlank(token) && this.authenticate(agentId, token)) {
            //鉴权成功，建立连接
            session.getUserProperties().put(AGENT_ID, agentId);
            session.getUserProperties().put("principal", new DefaultJWTCallerPrincipal(agentId, Map.of(AGENT_ID, agentId)));
            System.out.println("WebSocket connection opened for agent: " + agentId);
            linkManager.addAgentLink(agentId, session);
        } else {
            //鉴权失败
            session.close(CloseReasons.UNAUTHORIZED);
        }
    }

    private boolean ensureAgentIdValid(String agentId) {
        // 检查agentId是否符合预期格式或存在于系统中
        return linkManager.hasAgentId(agentId);
    }

    private boolean authenticate(String agentId, String token) {
        try {
            JsonWebToken jwt = parser.parse(token, ctxInfo);
            return Objects.equals(agentId, jwt.getClaim(AGENT_ID));
        } catch (ParseException e) {
            return false;
        }
    }
}




