package com.vitalink.platform.shared.infrastructure.configuration;

import com.vitalink.platform.care.domain.model.aggregates.MeasurementRange;
import com.vitalink.platform.care.infrastructure.persistence.jpa.MeasurementRangeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("prod")
public class MeasurementCatalogInitializer implements CommandLineRunner {
  private final MeasurementRangeRepository ranges;

  public MeasurementCatalogInitializer(MeasurementRangeRepository ranges) {
    this.ranges = ranges;
  }

  @Override
  @Transactional
  public void run(String... args) {
    for (var range : MeasurementRange.adultReferenceCatalog()) {
      if (!ranges.existsById(range.getId())) ranges.save(range);
    }
  }
}
