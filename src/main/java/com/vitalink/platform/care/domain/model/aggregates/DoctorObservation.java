package com.vitalink.platform.care.domain.model.aggregates;

import com.vitalink.platform.care.domain.model.valueobjects.AlertStatus;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
public class DoctorObservation {
  public static final int MAX_TEXT_LENGTH = 2000;
  @Id private String id;
  private String patientId;
  private String doctorId;
  private String doctorName;
  @Column(length = MAX_TEXT_LENGTH, nullable = false) private String text;
  private Instant createdAt;
  private Instant attendedAt;

  protected DoctorObservation() {}

  public DoctorObservation(String patientId, String doctorId, String doctorName, String text) {
    BusinessException.require(
        text != null && !text.isBlank() && text.length() <= MAX_TEXT_LENGTH,
        400, "Escribe una observación de hasta 2000 caracteres.");
    this.id = UUID.randomUUID().toString();
    this.patientId = patientId;
    this.doctorId = doctorId;
    this.doctorName = doctorName;
    this.text = text.trim();
    this.createdAt = Instant.now();
  }

  public void requireReviewBy(String patientId, String doctorId) {
    BusinessException.require(
        this.patientId.equals(patientId) && this.doctorId.equals(doctorId),
        403, "Observación no autorizada.");
    requireOpen();
  }

  public void requireCoveredAlert(CareAlert alert) {
    BusinessException.require(
        alert.getStatus() == AlertStatus.ATTENDED || !alert.getRaisedAt().isAfter(createdAt),
        409, "Hay nuevas alertas. Envía otra observación.");
  }

  public void attend() {
    requireOpen();
    attendedAt = Instant.now();
  }

  private void requireOpen() {
    BusinessException.require(attendedAt == null, 409, "Esta revisión ya fue atendida.");
  }
}
