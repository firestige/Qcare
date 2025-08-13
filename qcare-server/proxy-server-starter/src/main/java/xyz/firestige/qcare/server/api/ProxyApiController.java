package xyz.firestige.qcare.server.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * HTTP API 控制器
 * 提供RESTful接口
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProxyApiController {

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok()
                .entity("{\"status\":\"UP\",\"service\":\"proxy-server\"}")
                .build();
    }

    @GET
    @Path("/info")
    public Response info() {
        return Response.ok()
                .entity("{\"name\":\"Qcare Proxy Server\",\"version\":\"1.0-SNAPSHOT\"}")
                .build();
    }

    @POST
    @Path("/proxy/{target}")
    public Response proxy(@PathParam("target") String target, String payload) {
        // TODO: 实现代理转发逻辑
        return Response.ok()
                .entity("{\"message\":\"Proxy request to " + target + " received\"}")
                .build();
    }
}
