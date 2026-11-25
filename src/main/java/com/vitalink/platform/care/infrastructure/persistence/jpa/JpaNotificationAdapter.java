package com.vitalink.platform.care.infrastructure.persistence.jpa;

import com.vitalink.platform.care.domain.model.aggregates.ObservationNotification;
import com.vitalink.platform.care.domain.ports.NotificationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaNotificationAdapter implements NotificationRepository {
  private final ObservationNotificationRepository repository;

  public JpaNotificationAdapter(ObservationNotificationRepository repository) {
    this.repository = repository;
  }

  public ObservationNotification save(ObservationNotification value) { return repository.save(value); }
  public Optional<ObservationNotification> findNotification(String id) { return repository.findById(id); }
  public List<ObservationNotification> notificationsFor(String id) {
    return repository.findAllByRecipientIdOrderByCreatedAtDesc(id);
  }
  public List<ObservationNotification> pendingEmails() {
    return repository.findTop20ByEmailStatusOrderByCreatedAtAsc("PENDING");
  }
}
