package com.vitalink.platform.providers.interfaces.rest;

import com.vitalink.platform.providers.application.queryservices.MedicalRegistryQueryService;
import com.vitalink.platform.providers.interfaces.rest.resources.RegisteredDoctorResource;
import com.vitalink.platform.providers.interfaces.rest.transform.RegisteredDoctorResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/medical-registry")
@Tag(name = "Medical registry (fictional)")
public class FakeMedicalRegistryController {
  private final MedicalRegistryQueryService registry;

  public FakeMedicalRegistryController(MedicalRegistryQueryService registry) {
    this.registry = registry;
  }

  @GetMapping
  public List<RegisteredDoctorResource> list() {
    return registry.list().stream().map(RegisteredDoctorResourceAssembler::toResource).toList();
  }

  @GetMapping("/{license}")
  public RegisteredDoctorResource find(@PathVariable String license) {
    return RegisteredDoctorResourceAssembler.toResource(registry.find(license));
  }
}
