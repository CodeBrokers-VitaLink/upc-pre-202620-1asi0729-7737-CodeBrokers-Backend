package com.vitalink.platform.care.interfaces.rest.transform;

import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.interfaces.rest.resources.*;

public final class ObservationResourceFromEntityAssembler {
  private ObservationResourceFromEntityAssembler() {}

  public static ObservationResource toResource(DoctorObservation value) {
    return new ObservationResource(
        value.getId(), value.getPatientId(), value.getDoctorId(), value.getDoctorName(),
        value.getText(), value.getCreatedAt(), value.getAttendedAt());
  }

  public static NotificationResource toResource(ObservationNotification value) {
    return new NotificationResource(
        value.getId(), value.getRecipientId(), value.getObservationId(), value.getPatientId(),
        value.getDoctorName(), value.getCreatedAt(), value.getReadAt(),
        value.getEmailStatus(), value.getEmailAttempts());
  }
}
