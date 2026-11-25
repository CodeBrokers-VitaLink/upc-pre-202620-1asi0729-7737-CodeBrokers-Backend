package com.vitalink.platform.care.infrastructure.scheduling;

import com.vitalink.platform.care.application.commandservices.NotificationEmailDeliveryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ObservationEmailWorker {
  private final NotificationEmailDeliveryService delivery;

  public ObservationEmailWorker(NotificationEmailDeliveryService delivery) {
    this.delivery = delivery;
  }

  @Scheduled(fixedDelayString = "${app.observation-email-delay:60000}")
  public void deliver() { delivery.deliverPending(); }
}
