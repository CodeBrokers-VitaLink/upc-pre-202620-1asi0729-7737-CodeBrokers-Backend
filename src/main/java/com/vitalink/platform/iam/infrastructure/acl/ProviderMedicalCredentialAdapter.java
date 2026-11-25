package com.vitalink.platform.iam.infrastructure.acl;

import com.vitalink.platform.iam.domain.ports.MedicalCredentialValidation;
import com.vitalink.platform.providers.application.queryservices.MedicalRegistryQueryService;
import org.springframework.stereotype.Component;

@Component
public class ProviderMedicalCredentialAdapter implements MedicalCredentialValidation {
  private final MedicalRegistryQueryService registry;

  public ProviderMedicalCredentialAdapter(MedicalRegistryQueryService registry) {
    this.registry = registry;
  }

  public void validate(String license, String name) { registry.find(license).validateName(name); }
}
