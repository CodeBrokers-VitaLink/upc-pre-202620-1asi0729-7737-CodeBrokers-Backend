package com.vitalink.platform.care.application.internal.queryservices;

import com.vitalink.platform.care.application.queryservices.*;
import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.queries.GetPatientObservationsQuery;
import com.vitalink.platform.care.domain.ports.*;
import com.vitalink.platform.iam.application.queryservices.CurrentUserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObservationQueryServiceImpl implements ObservationQueryService {
  private final ObservationRepository observations;
  private final NotificationRepository notifications;
  private final CareAccessService access;
  private final CurrentUserService current;

  public ObservationQueryServiceImpl(
      ObservationRepository observations, NotificationRepository notifications,
      CareAccessService access, CurrentUserService current) {
    this.observations = observations;
    this.notifications = notifications;
    this.access = access;
    this.current = current;
  }

  public List<DoctorObservation> handle(GetPatientObservationsQuery query) {
    access.requirePatient(query.patientId());
    return observations.observationsFor(query.patientId());
  }

  public List<ObservationNotification> notifications() {
    return notifications.notificationsFor(current.require().getId());
  }
}
