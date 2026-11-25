package com.vitalink.platform.providers.application.queryservices;

import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/** Fictional registry for development; does not verify real professional credentials. */
@Service
public class FakeMedicalRegistry {
  public record RegisteredDoctor(String professionalLicense, String name) {}
  private final List<RegisteredDoctor> doctors;

  public FakeMedicalRegistry() throws IOException {
    var rows = new ArrayList<RegisteredDoctor>();
    try (var input = new ClassPathResource("fake-medical-registry.json").getInputStream()) {
      for (var node : JsonMapper.builder().build().readTree(input))
        rows.add(new RegisteredDoctor(node.get("professionalLicense").asText(), node.get("name").asText()));
    }
    doctors = List.copyOf(rows);
  }

  public List<RegisteredDoctor> list() { return doctors; }

  public RegisteredDoctor find(String license) {
    return doctors.stream().filter(d -> d.professionalLicense().equals(license)).findFirst()
        .orElseThrow(() -> new BusinessException(400, "El número de colegiatura no es válido."));
  }

  public void validate(String license, String name) {
    var doctor = find(license);
    BusinessException.require(normalize(doctor.name()).equals(normalize(name)), 400,
        "El nombre no coincide con la colegiatura registrada.");
  }

  private String normalize(String value) {
    if (value == null) return "";
    return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
        .trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
  }
}
