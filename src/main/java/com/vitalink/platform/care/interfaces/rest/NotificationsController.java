package com.vitalink.platform.care.interfaces.rest;

import com.vitalink.platform.care.application.commandservices.ObservationCommandService;
import com.vitalink.platform.care.application.queryservices.ObservationQueryService;
import com.vitalink.platform.care.domain.model.commands.ReadNotificationCommand;
import com.vitalink.platform.care.interfaces.rest.resources.NotificationResource;
import com.vitalink.platform.care.interfaces.rest.transform.ObservationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Care")
@SecurityRequirement(name = "bearerAuth")
public class NotificationsController {
  private final ObservationCommandService commands;
  private final ObservationQueryService queries;

  public NotificationsController(ObservationCommandService commands, ObservationQueryService queries) {
    this.commands = commands;
    this.queries = queries;
  }

  @GetMapping
  public List<NotificationResource> list() {
    return queries.notifications().stream()
        .map(ObservationResourceFromEntityAssembler::toResource).toList();
  }

  @PatchMapping("/{id}/read")
  public NotificationResource read(@PathVariable String id) {
    return ObservationResourceFromEntityAssembler.toResource(
        commands.handle(new ReadNotificationCommand(id)));
  }
}
