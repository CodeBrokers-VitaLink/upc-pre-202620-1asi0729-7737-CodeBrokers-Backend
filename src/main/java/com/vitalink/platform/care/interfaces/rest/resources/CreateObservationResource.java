package com.vitalink.platform.care.interfaces.rest.resources;

import com.vitalink.platform.care.domain.model.aggregates.DoctorObservation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateObservationResource(
    @NotBlank @Size(max = DoctorObservation.MAX_TEXT_LENGTH) String text) {}
