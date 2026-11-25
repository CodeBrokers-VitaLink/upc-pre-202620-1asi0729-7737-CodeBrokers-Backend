package com.vitalink.platform.care.infrastructure.persistence.jpa;
import com.vitalink.platform.care.domain.model.aggregates.DoctorObservation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DoctorObservationRepository extends JpaRepository<DoctorObservation,String> { java.util.List<DoctorObservation> findAllByPatientIdOrderByCreatedAtDesc(String patientId); }
