package com.vitalink.platform.providers.domain.ports;

import com.vitalink.platform.providers.domain.model.valueobjects.RegisteredDoctor;
import java.util.List;
import java.util.Optional;

public interface MedicalRegistry {
  List<RegisteredDoctor> list();
  Optional<RegisteredDoctor> find(String professionalLicense);
}
