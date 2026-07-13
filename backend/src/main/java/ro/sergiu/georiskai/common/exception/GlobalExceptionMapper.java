package ro.sergiu.georiskai.common.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ApiException apiException) {
            return response(apiException.status(), apiException.getMessage(), apiException.errorCode());
        }

        if (exception instanceof ConstraintViolationException) {
            return response(Response.Status.BAD_REQUEST, "Request validation failed", "VALIDATION_ERROR");
        }

        if (exception instanceof WebApplicationException webException) {
            Response.Status status = Response.Status.fromStatusCode(webException.getResponse().getStatus());
            Response.Status safeStatus = status == null ? Response.Status.INTERNAL_SERVER_ERROR : status;
            return response(safeStatus, safeStatus.getReasonPhrase(), safeStatus.name());
        }

        LOG.error("Unhandled API exception", exception);
        return response(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected error", "INTERNAL_SERVER_ERROR");
    }

    private Response response(Response.Status status, String message, String errorCode) {
        return Response.status(status)
                .entity(new ApiErrorResponse(message, errorCode))
                .build();
    }
}
