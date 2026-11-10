package com.vitalink.platform.care.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class MeasurementRange {
  @Id private String id;
  private String unit;
  private Double normalMin;
  private Double normalMax;
  private Double dangerBelow;
  private Double dangerFrom;
  private Double dangerAtOrBelow;
  private Double systolicDangerBelow;
  private Double systolicDangerFrom;
  private Double systolicDangerAtOrBelow;
  private Double diastolicDangerBelow;
  private Double diastolicDangerFrom;
  private Double diastolicDangerAtOrBelow;
  private Double systolicMin;
  private Double systolicMax;
  private Double diastolicMin;
  private Double diastolicMax;

  protected MeasurementRange() {}

  public MeasurementRange(
      String id, String unit, Double min, Double max, Double below, Double from) {
    this.id = id;
    this.unit = unit;
    normalMin = min;
    normalMax = max;
    dangerBelow = below;
    dangerFrom = from;
  }

  public static MeasurementRange bloodPressure() {
    var range = new MeasurementRange("bloodPressure", "mmHg", null, null, null, null);
    range.systolicMin = 90d;
    range.systolicMax = 120d;
    range.diastolicMin = 60d;
    range.diastolicMax = 80d;
    return range;
  }

  /** Default adult reference ranges for visual review, not diagnostic criteria. */
  public static java.util.List<MeasurementRange> adultReferenceCatalog() {
    var oxygen = new MeasurementRange("oxygen", "%", 95d, 100d, null, null);
    oxygen.dangerAtOrBelow = 88d;
    var pressure = bloodPressure();
    pressure.systolicMax = Math.nextDown(120d);
    pressure.diastolicMax = Math.nextDown(80d);
    // Measurements may contain decimals; values above 180/120 are high alert.
    pressure.systolicDangerFrom = Math.nextUp(180d);
    pressure.diastolicDangerFrom = Math.nextUp(120d);
    return java.util.List.of(
        new MeasurementRange("heartRate", "bpm", 60d, 100d, 40d, 150d),
        oxygen,
        new MeasurementRange("glucose", "mg/dL", 70d, 99d, 55d, 300d),
        pressure);
  }
}
