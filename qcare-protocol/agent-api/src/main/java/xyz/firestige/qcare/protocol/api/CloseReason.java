package xyz.firestige.qcare.protocol.api;

public final class CloseReason {
    public static final CloseReason BAD_REQUEST = new CloseReason(4000, "Bad Request");
    public static final CloseReason UNAUTHORIZED = new CloseReason(4001, "Unauthorized");
    public static final CloseReason FORBIDDEN = new CloseReason(4003, "Forbidden");
    public static final CloseReason NOT_FOUND = new CloseReason(4004, "Not Found");
    public static final CloseReason INTERNAL_SERVER_ERROR = new CloseReason(5000, "Internal Server Error");
    public static final CloseReason SERVICE_UNAVAILABLE = new CloseReason(5001, "Service Unavailable");
    public static final CloseReason REPLACED = new CloseReason(5002, "Replaced");
    private final int code;
    private final String reason;
    public CloseReason(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public int hashCode() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof CloseReason that && code == that.code);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[" + code + ": " + reason + "]";
    }
}
