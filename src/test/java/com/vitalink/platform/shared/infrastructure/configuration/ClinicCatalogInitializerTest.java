package com.vitalink.platform.shared.infrastructure.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.vitalink.platform.providers.domain.model.aggregates.Clinic;
import com.vitalink.platform.providers.infrastructure.persistence.jpa.ClinicRepository;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClinicCatalogInitializerTest {
  @Test
  void preservesExistingClinicsAndAddsOnlyMissingOnesAcrossRestarts() {
    Map<String, Clinic> rows = new LinkedHashMap<>();
    rows.put("CL-02", new Clinic("CL-02", "Nombre existente"));
    var repository = (ClinicRepository) Proxy.newProxyInstance(
        ClinicRepository.class.getClassLoader(), new Class<?>[] {ClinicRepository.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "existsById" -> rows.containsKey((String) args[0]);
          case "save" -> {
            var clinic = (Clinic) args[0];
            rows.put(clinic.getId(), clinic);
            yield clinic;
          }
          default -> throw new UnsupportedOperationException(method.getName());
        });
    var initializer = new ClinicCatalogInitializer(repository);
    initializer.run();
    initializer.run();
    assertEquals(3, rows.size());
    assertEquals("Nombre existente", rows.get("CL-02").getName());
    assertEquals("Clínica Central", rows.get("CL-01").getName());
    assertEquals("Clínica Vida", rows.get("CL-03").getName());
  }
}
