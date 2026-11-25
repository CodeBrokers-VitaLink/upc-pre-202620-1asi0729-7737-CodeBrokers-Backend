package com.vitalink.platform.shared.infrastructure.configuration;

import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.valueobjects.Severity;
import com.vitalink.platform.care.infrastructure.persistence.jpa.*;
import com.vitalink.platform.iam.domain.model.aggregates.User;
import com.vitalink.platform.iam.domain.model.valueobjects.Role;
import com.vitalink.platform.iam.infrastructure.persistence.jpa.UserRepository;
import com.vitalink.platform.providers.domain.model.aggregates.*;
import com.vitalink.platform.providers.infrastructure.persistence.jpa.*;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"dev", "test"})
@ConditionalOnProperty(name = "app.demo-data", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {
  private final UserRepository users;
  private final PasswordEncoder passwords;
  private final ClinicRepository clinics;
  private final DoctorRepository doctors;
  private final PatientRepository patients;
  private final CareRelationshipRepository relationships;
  private final ClinicalRecordRepository records;
  private final CareAlertRepository alerts;
  private final MeasurementRangeRepository ranges;

  public DemoDataInitializer(
      UserRepository u,
      PasswordEncoder pw,
      ClinicRepository c,
      DoctorRepository d,
      PatientRepository p,
      CareRelationshipRepository rel,
      ClinicalRecordRepository r,
      CareAlertRepository a,
      MeasurementRangeRepository m) {
    users = u;
    passwords = pw;
    clinics = c;
    doctors = d;
    patients = p;
    relationships = rel;
    records = r;
    alerts = a;
    ranges = m;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (users.existsById("DR-01")) return;
    clinics.saveAll(
        List.of(
            new Clinic("CL-01", "Clínica Central"),
            new Clinic("CL-02", "Clínica San José"),
            new Clinic("CL-03", "Clínica Vida")));
    users.saveAll(
        List.of(
            new User(
                "DR-01",
                "Ana Torres",
                "ana@vitalink.demo",
                passwords.encode("Demo12345"),
                Role.DOCTOR,
                "900000001"),
            new User(
                "DR-02",
                "Luis Ramos",
                "luis@vitalink.demo",
                passwords.encode("Demo12345"),
                Role.DOCTOR,
                "900000002"),
            new User(
                "DR-03",
                "Sofía Ruiz",
                "sofia@vitalink.demo",
                passwords.encode("Demo12345"),
                Role.DOCTOR,
                "900000005"),
            new User(
                "DR-04",
                "Diego Castro",
                "diego@vitalink.demo",
                passwords.encode("Demo12345"),
                Role.DOCTOR,
                "900000006"),
            new User("PT-01", "Elena Pérez", "elena@vitalink.demo", passwords.encode("Demo12345"), Role.PATIENT, "900000007"),
            new User(
                "FC-01",
                "María Pérez",
                "maria@vitalink.demo",
                passwords.encode("Demo12345"),
                Role.FAMILY,
                "900000003"),
            new User(
                "FC-02",
                "Pedro Díaz",
                "pedro@vitalink.demo",
                passwords.encode("Demo12345"),
                Role.FAMILY,
                "900000004")));
    doctors.saveAll(
        List.of(
            new Doctor("DR-01", "Ana Torres", "Medicina general", "10001", "clinic", "CL-01"),
            new Doctor("DR-02", "Luis Ramos", "Medicina general", "10002", "independent", null),
            new Doctor("DR-03", "Sofía Ruiz", "Medicina general", "10003", "clinic", "CL-02"),
            new Doctor("DR-04", "Diego Castro", "Medicina general", "10004", "clinic", "CL-03")));
    patients.saveAll(
        List.of(
            new Patient("P-001", "Elena", "Pérez", 72, "Lima", "DR-01", "PT-01", Set.of("FC-01")),
            new Patient("P-002", "Carlos", "Pérez", 68, "Lima", null, null, Set.of("FC-01")),
            new Patient("P-003", "Rosa", "Díaz", 75, "Lima", null, null, Set.of("FC-02"))));
    var relation = new CareRelationship("REL-001", "P-001", "DR-01");
    relation.respond("accept", "FC-01");
    relationships.save(relation);
    records.saveAll(
        List.of(
            new ClinicalRecord(
                "R-001", "P-001", "heartRate", 78, "bpm", "DR-01", "Ana Torres", null),
            new ClinicalRecord(
                "R-002", "P-002", "heartRate", 80, "bpm", "DR-01", "Ana Torres", null)));
    alerts.saveAll(
        List.of(
            new CareAlert("A-001", "P-001", "help", Severity.HIGH, null),
            new CareAlert("A-002", "P-002", "help", Severity.HIGH, null)));
    ranges.saveAll(
        List.of(
            new MeasurementRange("heartRate", "bpm", 60d, 100d, 40d, 140d),
            new MeasurementRange("oxygen", "%", 95d, 100d, 90d, null),
            new MeasurementRange("glucose", "mg/dL", 70d, 140d, 54d, 250d),
            MeasurementRange.bloodPressure()));
  }
}
