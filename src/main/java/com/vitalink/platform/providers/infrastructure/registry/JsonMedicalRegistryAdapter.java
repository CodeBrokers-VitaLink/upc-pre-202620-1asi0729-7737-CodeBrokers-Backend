package com.vitalink.platform.providers.infrastructure.registry;

import com.vitalink.platform.providers.domain.model.valueobjects.RegisteredDoctor;
import com.vitalink.platform.providers.domain.ports.MedicalRegistry;
import java.io.IOException;
import java.util.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/** Fictional data source. Replace this adapter to use an authorized external registry. */
@Component
public class JsonMedicalRegistryAdapter implements MedicalRegistry {
  private final List<RegisteredDoctor> doctors;

  public JsonMedicalRegistryAdapter() throws IOException {
    var rows = new ArrayList<RegisteredDoctor>();
    try (var input = new ClassPathResource("fake-medical-registry.json").getInputStream()) {
      for (var node : JsonMapper.builder().build().readTree(input))
        rows.add(new RegisteredDoctor(
            node.get("professionalLicense").asText(), node.get("name").asText()));
    }
    doctors = List.copyOf(rows);
  }

  public List<RegisteredDoctor> list() { return doctors; }

  public Optional<RegisteredDoctor> find(String license) {
    return doctors.stream().filter(doctor -> doctor.professionalLicense().equals(license)).findFirst();
  }
}
