package com.vitalink.platform.care.infrastructure.persistence.jpa;

import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.ports.PatientReviewRepository;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaPatientReviewAdapter implements PatientReviewRepository {
  private final PatientRepository patients;
  private final CareAlertRepository alerts;

  public JpaPatientReviewAdapter(PatientRepository patients, CareAlertRepository alerts) {
    this.patients = patients;
    this.alerts = alerts;
  }

  public Patient lockPatient(String id) {
    return patients.findForUpdate(id)
        .orElseThrow(() -> new BusinessException(404, "Paciente no encontrado."));
  }

  public List<CareAlert> alertsFor(String id) { return alerts.findAllByPatientId(id); }
}
