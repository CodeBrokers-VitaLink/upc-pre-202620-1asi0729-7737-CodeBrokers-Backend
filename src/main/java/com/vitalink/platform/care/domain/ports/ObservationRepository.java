package com.vitalink.platform.care.domain.ports;

import com.vitalink.platform.care.domain.model.aggregates.*;
import java.util.List;
import java.util.Optional;

public interface ObservationRepository {
  DoctorObservation save(DoctorObservation observation);
  Optional<DoctorObservation> findObservation(String id);
  List<DoctorObservation> observationsFor(String patientId);
}
