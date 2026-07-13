package ro.sergiu.georiskai.common.exception;

import jakarta.ws.rs.core.Response;

public final class ApiException extends RuntimeException {

    private final Response.Status status;
    private final String errorCode;

    private ApiException(Response.Status status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(Response.Status.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(Response.Status.NOT_FOUND, "NOT_FOUND", message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(Response.Status.CONFLICT, "CONFLICT", message);
    }

    public Response.Status status() {
        return status;
    }

    public String errorCode() {
        return errorCode;
    }
}
