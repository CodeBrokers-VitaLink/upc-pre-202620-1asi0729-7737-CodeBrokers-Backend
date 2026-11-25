package com.vitalink.platform.care.interfaces.rest.resources;

import java.time.Instant;

public record ObservationResource(
    String id, String patientId, String doctorId, String doctorName,
    String text, Instant createdAt, Instant attendedAt) {}
