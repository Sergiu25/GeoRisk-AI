package ro.sergiu.georiskai.risk.controller;

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
import ro.sergiu.georiskai.risk.dto.RiskAssessmentDto;
import ro.sergiu.georiskai.risk.service.RiskAssessmentService;
import ro.sergiu.georiskai.common.exception.ApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Path("/api/risk-assessments")
@Tag(name = "Risk Assessments", description = "Risk assessment endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RiskAssessmentController {

    @Inject
    RiskAssessmentService riskService;

    @POST
    @Operation(summary = "Create a new risk assessment")
    @APIResponse(responseCode = "201", description = "Assessment created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RiskAssessmentDto.class)))
    public Response createAssessment(@NotNull @Valid RiskAssessmentDto dto) {
        RiskAssessmentDto created = riskService.createAssessment(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get risk assessment by ID")
    @APIResponse(responseCode = "200", description = "Assessment found")
    @APIResponse(responseCode = "404", description = "Assessment not found")
    public Response getAssessmentById(@PathParam("id") Long id) {
        RiskAssessmentDto assessment = riskService.getAssessmentById(id);
        if (assessment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(assessment).build();
    }

    @GET
    @Path("/location/{locationId}")
    @Operation(summary = "Get all assessments for a location")
    @APIResponse(responseCode = "200", description = "List of assessments for the location")
    public Response getAssessmentsByLocation(@PathParam("locationId") Long locationId) {
        List<RiskAssessmentDto> assessments = riskService.getAssessmentsByLocation(locationId);
        return Response.ok(assessments).build();
    }

    @GET
    @Path("/location/{locationId}/latest")
    @Operation(summary = "Get latest assessment for a location")
    @APIResponse(responseCode = "200", description = "Latest assessment for the location")
    @APIResponse(responseCode = "404", description = "No assessments found")
    public Response getLatestAssessment(@PathParam("locationId") Long locationId) {
        Optional<RiskAssessmentDto> assessment = riskService.getLatestAssessmentByLocation(locationId);
        return assessment
                .map(value -> Response.ok(value).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/location/{locationId}/range")
    @Operation(summary = "Get assessments for a location within a date range")
    @APIResponse(responseCode = "200", description = "Assessments in the date range")
    public Response getAssessmentsByDateRange(
            @PathParam("locationId") Long locationId,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            LocalDateTime startDate = parseDate(startDateStr, "startDate");
            LocalDateTime endDate = parseDate(endDateStr, "endDate");
            List<RiskAssessmentDto> assessments = riskService.getAssessmentsByLocationAndDateRange(locationId, startDate, endDate);
            return Response.ok(assessments).build();
        } catch (DateTimeParseException e) {
            throw ApiException.badRequest("Dates must use ISO-8601 local date-time format");
        }
    }

    @GET
    @Path("/level/{level}")
    @Operation(summary = "Get all assessments by risk level")
    @APIResponse(responseCode = "200", description = "Assessments with the specified level")
    public Response getAssessmentsByLevel(@PathParam("level") String level) {
        List<RiskAssessmentDto> assessments = riskService.getAssessmentsByLevel(level);
        return Response.ok(assessments).build();
    }

    @GET
    @Path("/critical")
    @Operation(summary = "Get all critical risk assessments")
    @APIResponse(responseCode = "200", description = "All critical risk assessments")
    public Response getCriticalRisks() {
        List<RiskAssessmentDto> assessments = riskService.getCriticalRisks();
        return Response.ok(assessments).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a risk assessment")
    @APIResponse(responseCode = "204", description = "Assessment deleted successfully")
    @APIResponse(responseCode = "404", description = "Assessment not found")
    public Response deleteAssessment(@PathParam("id") Long id) {
        boolean deleted = riskService.deleteAssessment(id);
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
