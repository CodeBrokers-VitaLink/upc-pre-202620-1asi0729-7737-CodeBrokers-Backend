package com.vitalink.platform.care.domain.ports;

import com.vitalink.platform.care.domain.model.aggregates.*;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
  ObservationNotification save(ObservationNotification notification);
  Optional<ObservationNotification> findNotification(String id);
  List<ObservationNotification> notificationsFor(String recipientId);
  List<ObservationNotification> pendingEmails();
}
