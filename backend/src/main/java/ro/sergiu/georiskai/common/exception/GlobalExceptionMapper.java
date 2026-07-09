package ro.sergiu.georiskai.common.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        ApiErrorResponse error = new ApiErrorResponse(
            exception.getMessage() == null ? "Unexpected error" : exception.getMessage(),
            "INTERNAL_SERVER_ERROR"
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error)
            .build();
    }
}
