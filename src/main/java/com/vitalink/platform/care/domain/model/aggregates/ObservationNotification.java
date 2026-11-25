package com.vitalink.platform.care.domain.model.aggregates;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
@Getter @Entity
public class ObservationNotification {
 @Id private String id;
 private String recipientId;
 private String observationId;
 private String patientId;
 private String doctorName;
 private Instant createdAt;
 private Instant readAt;
 private String emailStatus="PENDING";
 private int emailAttempts;
 protected ObservationNotification() {}
 public ObservationNotification(String recipientId, DoctorObservation o) {
  id=java.util.UUID.randomUUID().toString(); this.recipientId=recipientId;
  observationId=o.getId(); patientId=o.getPatientId(); doctorName=o.getDoctorName(); createdAt=o.getCreatedAt();
 }
 public void read() { if(readAt==null) readAt=Instant.now(); }
 public void emailAttempt(boolean success) { emailAttempts++; emailStatus=success?"SENT":emailAttempts>=5?"FAILED":"PENDING"; }
}
