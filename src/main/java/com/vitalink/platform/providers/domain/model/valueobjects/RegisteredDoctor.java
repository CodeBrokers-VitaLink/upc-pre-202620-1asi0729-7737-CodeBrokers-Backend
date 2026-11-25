package com.vitalink.platform.providers.domain.model.valueobjects;

import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import java.text.Normalizer;
import java.util.Locale;

public record RegisteredDoctor(String professionalLicense, String name) {
  public void validateName(String candidate) {
    BusinessException.require(normalize(name).equals(normalize(candidate)), 400,
        "El nombre no coincide con la colegiatura registrada.");
  }

  private static String normalize(String value) {
    if (value == null) return "";
    return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
        .trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
  }
}
