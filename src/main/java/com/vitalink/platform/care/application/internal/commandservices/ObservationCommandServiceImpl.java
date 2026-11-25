package com.vitalink.platform.care.application.internal.commandservices;

import com.vitalink.platform.care.application.commandservices.*;
import com.vitalink.platform.care.application.queryservices.CareAccessService;
import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.commands.*;
import com.vitalink.platform.care.domain.model.valueobjects.AlertStatus;
import com.vitalink.platform.care.domain.ports.*;
import com.vitalink.platform.iam.application.queryservices.CurrentUserService;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ObservationCommandServiceImpl implements ObservationCommandService {
  private final ObservationRepository observations;
  private final NotificationRepository notifications;
  private final PatientReviewRepository reviews;
  private final CareAccessService access;
  private final CurrentUserService current;
  private final CareCommandService careCommands;

  public ObservationCommandServiceImpl(
      ObservationRepository observations, NotificationRepository notifications,
      PatientReviewRepository reviews, CareAccessService access,
      CurrentUserService current, CareCommandService careCommands) {
    this.observations = observations;
    this.notifications = notifications;
    this.reviews = reviews;
    this.access = access;
    this.current = current;
    this.careCommands = careCommands;
  }

  public DoctorObservation handle(CreateObservationCommand command) {
    access.requireDoctor();
    access.requirePatient(command.patientId());
    var patient = reviews.lockPatient(command.patientId());
    BusinessException.require(access.visible(patient), 404, "Paciente no encontrado.");
    var doctor = current.require();
    var observation = observations.save(new DoctorObservation(
        patient.getId(), doctor.getId(), doctor.getName(), command.text()));
    var recipients = new HashSet<>(patient.getFamilyIds());
    if (patient.getUserId() != null) recipients.add(patient.getUserId());
    recipients.forEach(id -> notifications.save(new ObservationNotification(id, observation)));
    return observation;
  }

  public DoctorObservation handle(AttendPatientCommand command) {
    access.requireDoctor();
    access.requirePatient(command.patientId());
    var patient = reviews.lockPatient(command.patientId());
    BusinessException.require(access.visible(patient), 404, "Paciente no encontrado.");
    var observation = observations.findObservation(command.observationId())
        .orElseThrow(() -> new BusinessException(404, "Observación no encontrada."));
    observation.requireReviewBy(command.patientId(), current.require().getId());
    var history = observations.observationsFor(command.patientId());
    BusinessException.require(
        !history.isEmpty() && history.getFirst().getId().equals(observation.getId()),
        409, "Utiliza la observación más reciente.");
    var alerts = reviews.alertsFor(command.patientId());
    // Validate the complete review before making any transitions.
    alerts.forEach(observation::requireCoveredAlert);
    for (var alert : alerts) {
      if (alert.getStatus() != AlertStatus.ATTENDED)
        careCommands.handle(new ChangeAlertStatusCommand(
            alert.getId(), AlertStatus.ATTENDED, observation.getText(), alert.getVersion()));
    }
    observation.attend();
    return observations.save(observation);
  }

  public ObservationNotification handle(ReadNotificationCommand command) {
    var notification = notifications.findNotification(command.notificationId())
        .filter(value -> value.belongsTo(current.require().getId()))
        .orElseThrow(() -> new BusinessException(404, "Notificación no encontrada."));
    notification.read();
    return notifications.save(notification);
  }
}
