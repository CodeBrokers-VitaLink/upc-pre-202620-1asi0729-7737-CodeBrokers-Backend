package com.vitalink.platform.care.domain.model.valueobjects;

import java.time.Instant;

public record ObservationEmail(
    String notificationId, String recipientEmail, String doctorName, Instant observedAt) {}
