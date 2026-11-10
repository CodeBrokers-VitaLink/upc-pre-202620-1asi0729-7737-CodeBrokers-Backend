package com.vitalink.platform.shared.infrastructure.configuration;

import static org.junit.jupiter.api.Assertions.*;
import com.vitalink.platform.care.domain.model.aggregates.MeasurementRange;
import com.vitalink.platform.care.infrastructure.persistence.jpa.MeasurementRangeRepository;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MeasurementCatalogInitializerTest {
  @Test
  void fillsMissingRangesAndPreservesExistingConfigurationAcrossRestarts() {
    Map<String, MeasurementRange> rows = new LinkedHashMap<>();
    var existing = new MeasurementRange("glucose", "mg/dL", 75d, 110d, 55d, 300d);
    rows.put("glucose", existing);
    var repository = (MeasurementRangeRepository) Proxy.newProxyInstance(
        MeasurementRangeRepository.class.getClassLoader(), new Class<?>[] {MeasurementRangeRepository.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "existsById" -> rows.containsKey((String) args[0]);
          case "save" -> {
            var range = (MeasurementRange) args[0];
            rows.put(range.getId(), range);
            yield range;
          }
          default -> throw new UnsupportedOperationException(method.getName());
        });
    var initializer = new MeasurementCatalogInitializer(repository);
    initializer.run(); initializer.run();
    assertEquals(4, rows.size());
    assertSame(existing, rows.get("glucose"));
    assertTrue(200 > rows.get("heartRate").getDangerFrom());
    assertEquals(88d, rows.get("oxygen").getDangerAtOrBelow());
    assertTrue(rows.get("bloodPressure").getSystolicDangerFrom() > 180d);
  }
}
