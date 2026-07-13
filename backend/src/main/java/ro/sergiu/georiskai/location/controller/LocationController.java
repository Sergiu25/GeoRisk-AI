package ro.sergiu.georiskai.location.controller;

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
import ro.sergiu.georiskai.location.dto.LocationDto;
import ro.sergiu.georiskai.location.service.LocationService;
import ro.sergiu.georiskai.common.exception.ApiException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Path("/api/locations")
@Tag(name = "Locations", description = "Location management endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationController {

    @Inject
    LocationService locationService;

    @POST
    @Operation(summary = "Create a new location")
    @APIResponse(responseCode = "201", description = "Location created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LocationDto.class)))
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createLocation(@NotNull @Valid LocationDto locationDto) {
        LocationDto created = locationService.createLocation(locationDto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get location by ID")
    @APIResponse(responseCode = "200", description = "Location found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LocationDto.class)))
    @APIResponse(responseCode = "404", description = "Location not found")
    public Response getLocationById(@PathParam("id") Long id) {
        LocationDto location = locationService.getLocationById(id);
        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(location).build();
    }

    @GET
    @Operation(summary = "Get all locations")
    @APIResponse(responseCode = "200", description = "List of all locations",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    public Response getAllLocations() {
        List<LocationDto> locations = locationService.getAllLocations();
        return Response.ok(locations).build();
    }

    @GET
    @Path("/search/country")
    @Operation(summary = "Get locations by country")
    @APIResponse(responseCode = "200", description = "Locations found for the country")
    public Response getLocationsByCountry(@QueryParam("country") String country) {
        if (country == null || country.isBlank()) {
            throw ApiException.badRequest("Country parameter is required");
        }
        List<LocationDto> locations = locationService.getLocationsByCountry(country.trim());
        return Response.ok(locations).build();
    }

    @GET
    @Path("/search/name")
    @Operation(summary = "Search locations by name")
    @APIResponse(responseCode = "200", description = "Locations matching the name")
    public Response searchLocationsByName(@QueryParam("q") String query) {
        if (query == null || query.isBlank()) {
            throw ApiException.badRequest("Query parameter 'q' is required");
        }
        List<LocationDto> locations = locationService.searchLocationsByName(query.trim());
        return Response.ok(locations).build();
    }

    @GET
    @Path("/search/coordinates")
    @Operation(summary = "Get location by coordinates")
    @APIResponse(responseCode = "200", description = "Location found at coordinates")
    @APIResponse(responseCode = "404", description = "No location found at coordinates")
    public Response getLocationByCoordinates(@QueryParam("lat") BigDecimal latitude, @QueryParam("lon") BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both latitude (lat) and longitude (lon) parameters are required")
                    .build();
        }
        Optional<LocationDto> location = locationService.getLocationByCoordinates(latitude, longitude);
        if (location.isPresent()) {
            return Response.ok(location.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a location")
    @APIResponse(responseCode = "200", description = "Location updated successfully")
    @APIResponse(responseCode = "404", description = "Location not found")
    public Response updateLocation(@PathParam("id") Long id, @NotNull @Valid LocationDto locationDto) {
        LocationDto updated = locationService.updateLocation(id, locationDto);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a location")
    @APIResponse(responseCode = "204", description = "Location deleted successfully")
    @APIResponse(responseCode = "404", description = "Location not found")
    public Response deleteLocation(@PathParam("id") Long id) {
        boolean deleted = locationService.deleteLocation(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
