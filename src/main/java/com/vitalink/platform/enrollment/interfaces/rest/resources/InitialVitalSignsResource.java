package com.vitalink.platform.enrollment.interfaces.rest.resources;

import jakarta.validation.constraints.*;

public record InitialVitalSignsResource(
    @NotNull @DecimalMin("20") @DecimalMax("300") Double heartRate,
    @NotNull @DecimalMin("1") @DecimalMax("100") Double oxygen,
    @NotNull @DecimalMin("20") @DecimalMax("1000") Double glucose,
    @NotNull @DecimalMin("40") @DecimalMax("300") Double systolic,
    @NotNull @DecimalMin("20") @DecimalMax("200") Double diastolic) {}
