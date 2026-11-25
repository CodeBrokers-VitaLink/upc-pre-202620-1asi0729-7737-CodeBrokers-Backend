package com.vitalink.platform.care.application.commandservices;

import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.commands.*;

public interface ObservationCommandService {
  DoctorObservation handle(CreateObservationCommand command);
  DoctorObservation handle(AttendPatientCommand command);
  ObservationNotification handle(ReadNotificationCommand command);
}
