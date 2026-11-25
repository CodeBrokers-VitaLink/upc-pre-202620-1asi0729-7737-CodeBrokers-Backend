package com.vitalink.platform.providers.interfaces.rest;

import com.vitalink.platform.providers.application.queryservices.FakeMedicalRegistry;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-registry")
public class FakeMedicalRegistryController {
  private final FakeMedicalRegistry registry;
  public FakeMedicalRegistryController(FakeMedicalRegistry registry) { this.registry = registry; }
  @GetMapping
  public List<FakeMedicalRegistry.RegisteredDoctor> list() { return registry.list(); }
  @GetMapping("/{license}")
  public FakeMedicalRegistry.RegisteredDoctor find(@PathVariable String license) { return registry.find(license); }
}
