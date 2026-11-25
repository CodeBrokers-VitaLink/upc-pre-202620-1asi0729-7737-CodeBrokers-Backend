package com.vitalink.platform.architecture;

import static org.junit.jupiter.api.Assertions.*;
import com.vitalink.platform.care.application.internal.commandservices.*;
import com.vitalink.platform.care.application.internal.queryservices.ObservationQueryServiceImpl;
import com.vitalink.platform.care.interfaces.rest.*;
import com.vitalink.platform.providers.application.internal.queryservices.MedicalRegistryQueryServiceImpl;
import com.vitalink.platform.providers.interfaces.rest.FakeMedicalRegistryController;
import org.junit.jupiter.api.Test;

class ObservationArchitectureTest {
  @Test void newUseCasesDependOnPortsRatherThanInfrastructureImplementations() {
    for (var useCase : new Class<?>[] {
        ObservationCommandServiceImpl.class, ObservationQueryServiceImpl.class,
        NotificationEmailDeliveryServiceImpl.class, MedicalRegistryQueryServiceImpl.class}) {
      for (var constructor : useCase.getConstructors()) {
        for (var dependency : constructor.getParameterTypes()) {
          assertTrue(dependency.isInterface(), useCase.getSimpleName() + " must depend on interfaces");
          assertFalse(dependency.getPackageName().contains(".infrastructure."),
              useCase.getSimpleName() + " must not depend on infrastructure");
        }
      }
    }
  }

  @Test void newRestControllersDependOnlyOnApplicationContracts() {
    for (var controller : new Class<?>[] {
        ObservationsController.class, NotificationsController.class, FakeMedicalRegistryController.class}) {
      for (var constructor : controller.getConstructors()) {
        for (var dependency : constructor.getParameterTypes()) {
          assertTrue(dependency.isInterface());
          assertTrue(dependency.getPackageName().contains(".application."));
          assertFalse(dependency.getPackageName().contains(".internal."));
        }
      }
    }
  }
}
