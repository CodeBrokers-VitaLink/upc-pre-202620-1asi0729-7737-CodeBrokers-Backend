package com.vitalink.platform.enrollment.domain.model.valueobjects;

import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;

/** Format boundaries for the simulation, not clinical reference ranges. */
public record InitialVitalSigns(
    Double heartRate, Double oxygen, Double glucose, Double systolic, Double diastolic) {
  public InitialVitalSigns {
    valid(heartRate, 20, 300, "Frecuencia cardíaca");
    valid(oxygen, 1, 100, "Saturación de oxígeno");
    valid(glucose, 20, 1000, "Glucosa");
    valid(systolic, 40, 300, "Presión sistólica");
    valid(diastolic, 20, 200, "Presión diastólica");
    BusinessException.require(
        systolic > diastolic, 400, "La presión sistólica debe ser mayor que la diastólica.");
  }

  private static void valid(Double value, double min, double max, String label) {
    BusinessException.require(
        value != null && Double.isFinite(value) && value >= min && value <= max,
        400,
        label + " es obligatoria y debe estar dentro de los límites de la simulación.");
  }

  public String bloodPressure() {
    return number(systolic) + "/" + number(diastolic);
  }

  private static String number(Double value) {
    return java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
  }
}
