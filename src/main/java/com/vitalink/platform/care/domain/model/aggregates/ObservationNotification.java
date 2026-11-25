package com.vitalink.platform.care.domain.model.aggregates;

import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
public class ObservationNotification {
  private static final int MAX_EMAIL_ATTEMPTS = 5;
  @Id private String id;
  private String recipientId;
  private String observationId;
  private String patientId;
  private String doctorName;
  private Instant createdAt;
  private Instant readAt;
  private String emailStatus = "PENDING";
  private int emailAttempts;

  protected ObservationNotification() {}

  public ObservationNotification(String recipientId, DoctorObservation observation) {
    id = UUID.randomUUID().toString();
    this.recipientId = recipientId;
    observationId = observation.getId();
    patientId = observation.getPatientId();
    doctorName = observation.getDoctorName();
    createdAt = observation.getCreatedAt();
  }

  public boolean belongsTo(String userId) { return recipientId.equals(userId); }

  public void read() {
    if (readAt == null) readAt = Instant.now();
  }

  public void emailAttempt(boolean success) {
    BusinessException.require("PENDING".equals(emailStatus), 409, "El envío ya finalizó.");
    emailAttempts++;
    emailStatus = success ? "SENT" : emailAttempts >= MAX_EMAIL_ATTEMPTS ? "FAILED" : "PENDING";
  }
}
