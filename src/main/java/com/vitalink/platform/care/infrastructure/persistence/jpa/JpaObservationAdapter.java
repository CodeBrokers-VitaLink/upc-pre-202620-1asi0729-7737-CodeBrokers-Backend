package com.vitalink.platform.care.infrastructure.persistence.jpa;

import com.vitalink.platform.care.domain.model.aggregates.DoctorObservation;
import com.vitalink.platform.care.domain.ports.ObservationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaObservationAdapter implements ObservationRepository {
  private final DoctorObservationRepository repository;

  public JpaObservationAdapter(DoctorObservationRepository repository) {
    this.repository = repository;
  }

  public DoctorObservation save(DoctorObservation value) { return repository.save(value); }
  public Optional<DoctorObservation> findObservation(String id) { return repository.findById(id); }
  public List<DoctorObservation> observationsFor(String id) {
    return repository.findAllByPatientIdOrderByCreatedAtDesc(id);
  }
}
