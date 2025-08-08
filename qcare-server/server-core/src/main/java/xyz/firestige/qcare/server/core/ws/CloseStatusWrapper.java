package xyz.firestige.qcare.server.core.ws;

import org.springframework.web.reactive.socket.CloseStatus;
import xyz.firestige.qcare.protocol.api.CloseReason;

public final class CloseStatusWrapper {
    public static final CloseStatus BAD_REQUEST = wrap(CloseReason.BAD_REQUEST);
    public static final CloseStatus UNAUTHORIZED = wrap(CloseReason.UNAUTHORIZED);
    public static final CloseStatus FORBIDDEN = wrap(CloseReason.FORBIDDEN);
    public static final CloseStatus NOT_FOUND = wrap(CloseReason.NOT_FOUND);
    public static final CloseStatus SERVICE_UNAVAILABLE = wrap(CloseReason.SERVICE_UNAVAILABLE);
    public static final CloseStatus INTERNAL_SERVER_ERROR = wrap(CloseReason.INTERNAL_SERVER_ERROR);
    public static final CloseStatus REPLACED = wrap(CloseReason.REPLACED);

    private static CloseStatus wrap(CloseReason reason) {
        if (reason != null) {
            return new CloseStatus(reason.getCode(), reason.getReason());
        }  else {
            return null;
        }
    }

    private CloseStatusWrapper() {}
}
