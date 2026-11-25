package com.vitalink.platform.providers.application.internal.queryservices;

import com.vitalink.platform.providers.application.queryservices.MedicalRegistryQueryService;
import com.vitalink.platform.providers.domain.model.valueobjects.RegisteredDoctor;
import com.vitalink.platform.providers.domain.ports.MedicalRegistry;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MedicalRegistryQueryServiceImpl implements MedicalRegistryQueryService {
  private final MedicalRegistry registry;

  public MedicalRegistryQueryServiceImpl(MedicalRegistry registry) { this.registry = registry; }

  public List<RegisteredDoctor> list() { return registry.list(); }

  public RegisteredDoctor find(String license) {
    return registry.find(license)
        .orElseThrow(() -> new BusinessException(400, "El número de colegiatura no es válido."));
  }
}
