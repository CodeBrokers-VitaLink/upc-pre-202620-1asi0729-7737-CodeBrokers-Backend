package com.vitalink.platform.care.interfaces.rest;
import com.vitalink.platform.care.application.internal.commandservices.ObservationService;
import com.vitalink.platform.care.domain.model.aggregates.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/v1")
public class ObservationsController {
 private final ObservationService service;
 public ObservationsController(ObservationService service) { this.service=service; }
 public record CreateObservation(@NotBlank @Size(max=2000) String text) {}
 @GetMapping("/patients/{id}/observations")
 public List<DoctorObservation> list(@PathVariable String id) { return service.list(id); }
 @PostMapping("/patients/{id}/observations") @ResponseStatus(HttpStatus.CREATED)
 public DoctorObservation create(@PathVariable String id,@Valid @RequestBody CreateObservation body) { return service.create(id,body.text()); }
 @PostMapping("/patients/{id}/observations/{observationId}/attend")
 public DoctorObservation attend(@PathVariable String id,@PathVariable String observationId) { return service.attend(id,observationId); }
 @GetMapping("/notifications")
 public List<ObservationNotification> notifications() { return service.notifications(); }
 @PatchMapping("/notifications/{id}/read")
 public ObservationNotification read(@PathVariable String id) { return service.read(id); }
}
