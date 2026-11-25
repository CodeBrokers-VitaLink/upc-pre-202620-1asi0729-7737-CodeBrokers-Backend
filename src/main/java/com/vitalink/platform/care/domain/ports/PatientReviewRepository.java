package com.vitalink.platform.care.domain.ports;

import com.vitalink.platform.care.domain.model.aggregates.*;
import java.util.List;
import java.util.Optional;

public interface PatientReviewRepository {
  Patient lockPatient(String patientId);
  List<CareAlert> alertsFor(String patientId);
}
