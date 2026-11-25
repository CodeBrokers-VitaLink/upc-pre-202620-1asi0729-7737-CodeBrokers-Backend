package com.vitalink.platform.care.interfaces.rest.resources;

import java.time.Instant;

public record NotificationResource(
    String id, String recipientId, String observationId, String patientId,
    String doctorName, Instant createdAt, Instant readAt, String emailStatus, int emailAttempts) {}
