package com.vitalink.platform.care.application;

import static org.junit.jupiter.api.Assertions.*;
import com.vitalink.platform.care.application.internal.commandservices.NotificationEmailDeliveryServiceImpl;
import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.valueobjects.ObservationEmail;
import com.vitalink.platform.care.domain.ports.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class NotificationEmailDeliveryTest {
  private static class Queue implements NotificationRepository {
    final ObservationNotification notification = new ObservationNotification(
        "PT-1", new DoctorObservation("P-1", "DR-1", "Ana", "Texto clínico privado"));
    public ObservationNotification save(ObservationNotification value) { return value; }
    public Optional<ObservationNotification> findNotification(String id) { return Optional.of(notification); }
    public List<ObservationNotification> notificationsFor(String id) { return List.of(notification); }
    public List<ObservationNotification> pendingEmails() {
      return "PENDING".equals(notification.getEmailStatus()) ? List.of(notification) : List.of();
    }
  }

  private static class Sender implements ObservationEmailSender {
    boolean configured = true;
    boolean fail;
    int calls;
    ObservationEmail last;
    public boolean isConfigured() { return configured; }
    public void send(ObservationEmail value) {
      calls++;
      last = value;
      if (fail) throw new IllegalStateException("Provider unavailable");
    }
  }

  @Test void keepsTheQueueUntouchedWithoutEmailConfiguration() {
    var queue = new Queue();
    var sender = new Sender();
    sender.configured = false;
    delivery(queue, sender).deliverPending();
    assertEquals("PENDING", queue.notification.getEmailStatus());
    assertEquals(0, queue.notification.getEmailAttempts());
    assertEquals(0, sender.calls);
  }

  @Test void retriesProviderFailuresUpToFiveTimesWithoutLosingTheNotification() {
    var queue = new Queue();
    var sender = new Sender();
    sender.fail = true;
    var delivery = delivery(queue, sender);
    for (int attempt = 0; attempt < 6; attempt++) delivery.deliverPending();
    assertEquals("FAILED", queue.notification.getEmailStatus());
    assertEquals(5, queue.notification.getEmailAttempts());
    assertEquals(5, sender.calls);
    assertNull(queue.notification.getReadAt());
  }

  @Test void marksAcceptedEmailSentAndDoesNotSendItAgain() {
    var queue = new Queue();
    var sender = new Sender();
    var delivery = delivery(queue, sender);
    delivery.deliverPending();
    delivery.deliverPending();
    assertEquals("SENT", queue.notification.getEmailStatus());
    assertEquals(1, sender.calls);
    assertEquals(queue.notification.getId(), sender.last.notificationId());
    assertEquals("patient@example.com", sender.last.recipientEmail());
  }

  @Test void missingRecipientUsesTheSameBoundedRetryPolicy() {
    var queue = new Queue();
    var sender = new Sender();
    var delivery = new NotificationEmailDeliveryServiceImpl(queue, id -> Optional.empty(), sender);
    delivery.deliverPending();
    assertEquals("PENDING", queue.notification.getEmailStatus());
    assertEquals(1, queue.notification.getEmailAttempts());
    assertEquals(0, sender.calls);
  }

  private NotificationEmailDeliveryServiceImpl delivery(Queue queue, Sender sender) {
    return new NotificationEmailDeliveryServiceImpl(
        queue, id -> Optional.of("patient@example.com"), sender);
  }
}
