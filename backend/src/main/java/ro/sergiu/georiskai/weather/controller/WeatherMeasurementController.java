package ro.sergiu.georiskai.weather.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ro.sergiu.georiskai.weather.dto.WeatherMeasurementDto;
import ro.sergiu.georiskai.weather.service.WeatherMeasurementService;
import ro.sergiu.georiskai.common.exception.ApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Path("/api/weather")
@Tag(name = "Weather", description = "Weather measurements endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeatherMeasurementController {

    @Inject
    WeatherMeasurementService weatherService;

    @POST
    @Operation(summary = "Record a weather measurement")
    @APIResponse(responseCode = "201", description = "Measurement recorded successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = WeatherMeasurementDto.class)))
    public Response recordMeasurement(@NotNull @Valid WeatherMeasurementDto dto) {
        WeatherMeasurementDto recorded = weatherService.recordMeasurement(dto);
        return Response.status(Response.Status.CREATED).entity(recorded).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get weather measurement by ID")
    @APIResponse(responseCode = "200", description = "Measurement found")
    @APIResponse(responseCode = "404", description = "Measurement not found")
    public Response getMeasurementById(@PathParam("id") Long id) {
        WeatherMeasurementDto measurement = weatherService.getMeasurementById(id);
        if (measurement == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(measurement).build();
    }

    @GET
    @Path("/location/{locationId}")
    @Operation(summary = "Get all measurements for a location")
    @APIResponse(responseCode = "200", description = "List of measurements for the location")
    public Response getMeasurementsByLocation(@PathParam("locationId") Long locationId) {
        List<WeatherMeasurementDto> measurements = weatherService.getMeasurementsByLocation(locationId);
        return Response.ok(measurements).build();
    }

    @GET
    @Path("/location/{locationId}/recent")
    @Operation(summary = "Get recent measurements for a location")
    @APIResponse(responseCode = "200", description = "Recent measurements for the location")
    public Response getRecentMeasurements(@PathParam("locationId") Long locationId, @QueryParam("limit") @DefaultValue("10") int limit) {
        List<WeatherMeasurementDto> measurements = weatherService.getRecentMeasurements(locationId, limit);
        return Response.ok(measurements).build();
    }

    @GET
    @Path("/location/{locationId}/range")
    @Operation(summary = "Get measurements for a location within a date range")
    @APIResponse(responseCode = "200", description = "Measurements in the date range")
    public Response getMeasurementsByDateRange(
            @PathParam("locationId") Long locationId,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            LocalDateTime startDate = parseDate(startDateStr, "startDate");
            LocalDateTime endDate = parseDate(endDateStr, "endDate");
            List<WeatherMeasurementDto> measurements = weatherService.getMeasurementsByLocationAndDateRange(locationId, startDate, endDate);
            return Response.ok(measurements).build();
        } catch (DateTimeParseException e) {
            throw ApiException.badRequest("Dates must use ISO-8601 local date-time format");
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a weather measurement")
    @APIResponse(responseCode = "204", description = "Measurement deleted successfully")
    @APIResponse(responseCode = "404", description = "Measurement not found")
    public Response deleteMeasurement(@PathParam("id") Long id) {
        boolean deleted = weatherService.deleteMeasurement(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private LocalDateTime parseDate(String value, String parameterName) {
        if (value == null || value.isBlank()) {
            throw ApiException.badRequest(parameterName + " parameter is required");
        }
        return LocalDateTime.parse(value);
    }
}
