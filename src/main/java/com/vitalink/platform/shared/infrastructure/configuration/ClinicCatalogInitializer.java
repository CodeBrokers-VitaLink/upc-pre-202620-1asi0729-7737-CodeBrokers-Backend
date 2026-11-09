package com.vitalink.platform.shared.infrastructure.configuration;

import com.vitalink.platform.providers.domain.model.aggregates.Clinic;
import com.vitalink.platform.providers.infrastructure.persistence.jpa.ClinicRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Keeps the production registration catalog available without loading demo accounts. */
@Component
@Profile("prod")
public class ClinicCatalogInitializer implements CommandLineRunner {
  private final ClinicRepository clinics;

  public ClinicCatalogInitializer(ClinicRepository clinics) {
    this.clinics = clinics;
  }

  @Override
  @Transactional
  public void run(String... args) {
    for (var clinic : List.of(
        new Clinic("CL-01", "Clínica Central"),
        new Clinic("CL-02", "Clínica San José"),
        new Clinic("CL-03", "Clínica Vida"))) {
      if (!clinics.existsById(clinic.getId())) {
        clinics.save(clinic);
      }
    }
  }
}
