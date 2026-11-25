package com.vitalink.platform.care.interfaces.rest;

import com.vitalink.platform.care.application.commandservices.ObservationCommandService;
import com.vitalink.platform.care.application.queryservices.ObservationQueryService;
import com.vitalink.platform.care.domain.model.commands.AttendPatientCommand;
import com.vitalink.platform.care.domain.model.queries.GetPatientObservationsQuery;
import com.vitalink.platform.care.interfaces.rest.resources.*;
import com.vitalink.platform.care.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Care")
@SecurityRequirement(name = "bearerAuth")
public class ObservationsController {
  private final ObservationCommandService commands;
  private final ObservationQueryService queries;

  public ObservationsController(ObservationCommandService commands, ObservationQueryService queries) {
    this.commands = commands;
    this.queries = queries;
  }

  @GetMapping("/patients/{id}/observations")
  public List<ObservationResource> list(@PathVariable String id) {
    return queries.handle(new GetPatientObservationsQuery(id)).stream()
        .map(ObservationResourceFromEntityAssembler::toResource).toList();
  }

  @PostMapping("/patients/{id}/observations")
  @ResponseStatus(HttpStatus.CREATED)
  public ObservationResource create(
      @PathVariable String id, @Valid @RequestBody CreateObservationResource resource) {
    return ObservationResourceFromEntityAssembler.toResource(
        commands.handle(ObservationCommandFromResourceAssembler.toCommand(id, resource)));
  }

  @PostMapping("/patients/{id}/observations/{observationId}/attend")
  public ObservationResource attend(@PathVariable String id, @PathVariable String observationId) {
    return ObservationResourceFromEntityAssembler.toResource(
        commands.handle(new AttendPatientCommand(id, observationId)));
  }
}
