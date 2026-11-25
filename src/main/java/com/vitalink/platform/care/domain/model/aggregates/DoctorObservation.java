package com.vitalink.platform.care.domain.model.aggregates;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
@Getter @Entity
public class DoctorObservation {
 @Id private String id;
 private String patientId;
 private String doctorId;
 private String doctorName;
 @Column(length=2000, nullable=false) private String text;
 private Instant createdAt;
 private Instant attendedAt;
 protected DoctorObservation() {}
 public DoctorObservation(String patientId, String doctorId, String doctorName, String text) {
  this.id=java.util.UUID.randomUUID().toString(); this.patientId=patientId;
  this.doctorId=doctorId; this.doctorName=doctorName; this.text=text; this.createdAt=Instant.now();
 }
 public void attend() { attendedAt=Instant.now(); }
}
