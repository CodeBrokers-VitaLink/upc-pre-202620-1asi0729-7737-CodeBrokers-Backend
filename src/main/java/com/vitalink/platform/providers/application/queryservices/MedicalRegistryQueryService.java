package com.vitalink.platform.providers.application.queryservices;

import com.vitalink.platform.providers.domain.model.valueobjects.RegisteredDoctor;
import java.util.List;

public interface MedicalRegistryQueryService {
  List<RegisteredDoctor> list();
  RegisteredDoctor find(String license);
}
