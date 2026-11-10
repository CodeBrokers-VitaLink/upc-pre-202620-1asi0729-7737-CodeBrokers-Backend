package com.vitalink.platform.enrollment.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;
import com.vitalink.platform.enrollment.interfaces.rest.resources.InitialVitalSignsResource;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

class InitialVitalSignsTest {
  @Test
  void allowsSevereReadingsWithinSupportedInputLimits() {
    assertDoesNotThrow(() -> new InitialVitalSigns(200.0, 67.0, 300.0, 190.0, 121.0));
  }

  @Test
  void rejectsInvertedPressureAndOutOfRangeValues() {
    assertThrows(BusinessException.class, () -> new InitialVitalSigns(70.0, 97.0, 90.0, 56.0, 67.0));
    assertThrows(BusinessException.class, () -> new InitialVitalSigns(19.0, 97.0, 90.0, 110.0, 70.0));
    assertThrows(BusinessException.class, () -> new InitialVitalSigns(70.0, 101.0, 90.0, 110.0, 70.0));
    assertThrows(BusinessException.class, () -> new InitialVitalSigns(70.0, 97.0, Double.NaN, 110.0, 70.0));
  }

  @Test
  void apiConstraintsMatchSupportedMinimumAndMaximumValues() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      var validator = factory.getValidator();
      assertTrue(validator.validate(new InitialVitalSignsResource(20.0, 1.0, 20.0, 40.0, 20.0)).isEmpty());
      assertTrue(validator.validate(new InitialVitalSignsResource(300.0, 100.0, 1000.0, 300.0, 200.0)).isEmpty());
      assertEquals(5, validator.validate(new InitialVitalSignsResource(19.0, 0.0, 19.0, 39.0, 19.0)).size());
      assertEquals(5, validator.validate(new InitialVitalSignsResource(301.0, 101.0, 1001.0, 301.0, 201.0)).size());
    }
  }
}
