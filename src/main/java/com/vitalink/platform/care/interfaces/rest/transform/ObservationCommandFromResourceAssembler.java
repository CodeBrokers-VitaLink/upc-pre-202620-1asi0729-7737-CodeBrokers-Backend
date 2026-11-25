package com.vitalink.platform.care.interfaces.rest.transform;

import com.vitalink.platform.care.domain.model.commands.CreateObservationCommand;
import com.vitalink.platform.care.interfaces.rest.resources.CreateObservationResource;

public final class ObservationCommandFromResourceAssembler {
  private ObservationCommandFromResourceAssembler() {}

  public static CreateObservationCommand toCommand(String patientId, CreateObservationResource resource) {
    return new CreateObservationCommand(patientId, resource.text());
  }
}
