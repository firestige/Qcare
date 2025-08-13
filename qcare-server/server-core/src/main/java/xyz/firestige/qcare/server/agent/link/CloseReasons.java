package xyz.firestige.qcare.server.agent.link;

import jakarta.websocket.CloseReason;

public final class CloseReasons {
    public static final CloseReason BAD_REQUEST = new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Bad request");
    public static final CloseReason UNAUTHORIZED = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized access");

    private CloseReasons() {
        // Prevent instantiation
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
