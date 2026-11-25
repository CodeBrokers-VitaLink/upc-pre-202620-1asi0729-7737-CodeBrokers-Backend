package com.vitalink.platform.providers.interfaces.rest.transform;

import com.vitalink.platform.providers.domain.model.valueobjects.RegisteredDoctor;
import com.vitalink.platform.providers.interfaces.rest.resources.RegisteredDoctorResource;

public final class RegisteredDoctorResourceAssembler {
  private RegisteredDoctorResourceAssembler() {}

  public static RegisteredDoctorResource toResource(RegisteredDoctor value) {
    return new RegisteredDoctorResource(value.professionalLicense(), value.name());
  }
}
