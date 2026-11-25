package com.vitalink.platform.care.application.queryservices;

import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.queries.GetPatientObservationsQuery;
import java.util.List;

public interface ObservationQueryService {
  List<DoctorObservation> handle(GetPatientObservationsQuery query);
  List<ObservationNotification> notifications();
}
