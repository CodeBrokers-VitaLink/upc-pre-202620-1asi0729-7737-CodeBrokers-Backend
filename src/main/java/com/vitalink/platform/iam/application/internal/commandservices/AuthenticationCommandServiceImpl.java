package com.vitalink.platform.iam.application.internal.commandservices;

import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.infrastructure.persistence.jpa.*;
import com.vitalink.platform.iam.application.commandservices.*;
import com.vitalink.platform.iam.domain.model.aggregates.*;
import com.vitalink.platform.iam.domain.model.commands.*;
import com.vitalink.platform.iam.domain.model.valueobjects.*;
import com.vitalink.platform.iam.domain.ports.MedicalCredentialValidation;
import com.vitalink.platform.iam.infrastructure.persistence.jpa.*;
import com.vitalink.platform.iam.infrastructure.security.TokenCodec;
import com.vitalink.platform.providers.domain.model.aggregates.Doctor;
import com.vitalink.platform.providers.infrastructure.persistence.jpa.*;
import com.vitalink.platform.shared.application.result.Result;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import java.time.Instant;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationCommandServiceImpl implements AuthenticationCommandService {
  private final MedicalCredentialValidation registry;
  private final UserRepository users;
  private final LoginAliasRepository aliases;
  private final AuthSessionRepository sessions;
  private final PasswordEncoder passwords;
  private final DoctorRepository doctors;
  private final ClinicRepository clinics;
  private final PatientRepository patients;
  private final CareRelationshipRepository relationships;

  public AuthenticationCommandServiceImpl(
      UserRepository users,
      LoginAliasRepository aliases,
      AuthSessionRepository sessions,
      PasswordEncoder passwords,
      DoctorRepository doctors,
      ClinicRepository clinics,
      PatientRepository patients,
      CareRelationshipRepository relationships,
      MedicalCredentialValidation registry) {
    this.registry = registry;
    this.users = users;
    this.aliases = aliases;
    this.sessions = sessions;
    this.passwords = passwords;
    this.doctors = doctors;
    this.clinics = clinics;
    this.patients = patients;
    this.relationships = relationships;
  }

  public Result<User, RegistrationFailure> handle(SignUpCommand c) {
    var email = new Email(c.email()).value();
    var role = Role.from(c.role());
    if (role == Role.DOCTOR) registry.validate(c.professionalLicense(), c.name());
    if (users.existsByEmail(email)
        || aliases.existsById(email)
        || role == Role.DOCTOR && doctors.existsByProfessionalLicense(c.professionalLicense()))
      return Result.failure(new RegistrationFailure.Duplicate());
    if (role == Role.DOCTOR) {
      BusinessException.require(
          c.specialty() != null
              && !c.specialty().isBlank()
              && c.professionalLicense() != null
              && c.professionalLicense().matches("[0-9]{4,10}"),
          400,
          "Especialidad y colegiatura válidas son obligatorias.");
      BusinessException.require(
          c.practiceType() != null && Set.of("clinic", "independent").contains(c.practiceType()),
          400,
          "Modalidad inválida.");
      BusinessException.require(
          !"clinic".equals(c.practiceType())
              || c.clinicId() != null && clinics.existsById(c.clinicId()),
          400,
          "Clínica inválida.");
    }
    Doctor selected = null;
    if (role == Role.PATIENT) {
      BusinessException.require(
          Boolean.TRUE.equals(c.consent()),
          400,
          "Se requiere autorización para el médico seleccionado.");
      BusinessException.require(
          c.age() != null
              && c.age() >= 0
              && c.age() <= 120
              && c.location() != null
              && !c.location().isBlank(),
          400,
          "Edad y ubicación obligatorias.");
      selected =
          doctors
              .findById(c.doctorId() == null ? "" : c.doctorId())
              .orElseThrow(() -> new BusinessException(400, "Médico inválido."));
      BusinessException.require(
          selected.getPracticeType().equals(c.serviceType()),
          400,
          "Modalidad y médico no coinciden.");
      BusinessException.require(
          !"clinic".equals(c.serviceType()) || Objects.equals(selected.getClinicId(), c.clinicId()),
          400,
          "El médico no pertenece a la clínica elegida.");
    }
    var id =
        (role == Role.DOCTOR ? "DR-" : role == Role.FAMILY ? "FC-" : "PT-") + UUID.randomUUID();
    var user =
        users.save(new User(id, c.name(), email, passwords.encode(c.password()), role, c.phone()));
    if (role == Role.DOCTOR)
      doctors.save(
          new Doctor(
              id,
              c.name(),
              c.specialty(),
              c.professionalLicense(),
              c.practiceType(),
              c.clinicId()));
    if (role == Role.PATIENT) {
      var parts = c.name().trim().split("\\s+", 2);
      var patient =
          patients.save(
              new Patient(
                  "P-" + UUID.randomUUID(),
                  parts[0],
                  parts.length > 1 ? parts[1] : "",
                  c.age(),
                  c.location(),
                  selected.getId(),
                  id,
                  Set.of()));
      var relation =
          new CareRelationship("REL-" + UUID.randomUUID(), patient.getId(), selected.getId());
      relation.respond("accept", id);
      relationships.save(relation);
    }
    return Result.success(user);
  }

  public AuthResult handle(SignInCommand c) {
    var email = new Email(c.email()).value();
    var primary = users.findByEmail(email);
    User user;
    String hash;
    if (primary.isPresent()) {
      user = primary.get();
      hash = user.getPasswordHash();
    } else {
      var alias =
          aliases
              .findById(email)
              .orElseThrow(() -> new BusinessException(401, "Credenciales inválidas."));
      user =
          users
              .findById(alias.getUserId())
              .orElseThrow(() -> new BusinessException(401, "Credenciales inválidas."));
      hash = alias.getPasswordHash();
    }
    BusinessException.require(
        passwords.matches(c.password(), hash), 401, "Credenciales inválidas.");
    var bytes = new byte[32];
    new java.security.SecureRandom().nextBytes(bytes);
    var token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    var expires = Instant.now().plusSeconds(8 * 3600);
    sessions.save(new AuthSession(TokenCodec.hash(token), user.getId(), expires));
    return new AuthResult(user, token, expires);
  }

  public void signOut(String token) {
    sessions.deleteById(TokenCodec.hash(token));
  }
}
