package com.vitalink.platform.iam.domain.ports;

/** IAM requests validation without depending on the provider registry implementation. */
public interface MedicalCredentialValidation {
  void validate(String professionalLicense, String name);
}
