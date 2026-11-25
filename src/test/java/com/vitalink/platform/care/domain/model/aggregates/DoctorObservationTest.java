package com.vitalink.platform.care.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.*;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import org.junit.jupiter.api.Test;

class DoctorObservationTest {
  @Test void rejectsInvalidTextWithoutDependingOnRestValidation() {
    assertThrows(BusinessException.class, () -> observation(null));
    assertThrows(BusinessException.class, () -> observation("   "));
    assertThrows(BusinessException.class, () -> observation("a".repeat(2001)));
    assertEquals("Indicaciones", observation("  Indicaciones  ").getText());
  }

  @Test void onlyItsDoctorCanCompleteAnOpenReview() {
    var observation = observation("Revisión");
    assertThrows(BusinessException.class, () -> observation.requireReviewBy("P-1", "DR-2"));
    assertThrows(BusinessException.class, () -> observation.requireReviewBy("P-2", "DR-1"));
    observation.requireReviewBy("P-1", "DR-1");
    observation.attend();
    assertNotNull(observation.getAttendedAt());
    assertThrows(BusinessException.class, observation::attend);
  }

  private DoctorObservation observation(String text) {
    return new DoctorObservation("P-1", "DR-1", "Ana", text);
  }
}
