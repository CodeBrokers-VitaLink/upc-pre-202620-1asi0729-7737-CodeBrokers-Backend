package com.vitalink.platform.care.application.internal.commandservices;

import com.vitalink.platform.care.application.commandservices.NotificationEmailDeliveryService;
import com.vitalink.platform.care.domain.model.valueobjects.ObservationEmail;
import com.vitalink.platform.care.domain.ports.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationEmailDeliveryServiceImpl implements NotificationEmailDeliveryService {
  private final NotificationRepository notifications;
  private final NotificationRecipientDirectory recipients;
  private final ObservationEmailSender sender;

  public NotificationEmailDeliveryServiceImpl(
      NotificationRepository notifications, NotificationRecipientDirectory recipients,
      ObservationEmailSender sender) {
    this.notifications = notifications;
    this.recipients = recipients;
    this.sender = sender;
  }

  @Transactional
  public void deliverPending() {
    if (!sender.isConfigured()) return;
    for (var notification : notifications.pendingEmails()) {
      var recipient = recipients.emailFor(notification.getRecipientId());
      boolean sent = false;
      if (recipient.isPresent()) {
        try {
          sender.send(new ObservationEmail(
              notification.getId(), recipient.get(), notification.getDoctorName(),
              notification.getCreatedAt()));
          sent = true;
        } catch (RuntimeException failure) {
          // Provider failures remain in the persistent queue for bounded retries.
        }
      }
      notification.emailAttempt(sent);
      notifications.save(notification);
    }
  }
}
