package com.vitalink.platform.care.infrastructure.persistence.jpa;
import com.vitalink.platform.care.domain.model.aggregates.ObservationNotification;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ObservationNotificationRepository extends JpaRepository<ObservationNotification,String> {
 java.util.List<ObservationNotification> findAllByRecipientIdOrderByCreatedAtDesc(String recipientId);
 java.util.List<ObservationNotification> findTop20ByEmailStatusOrderByCreatedAtAsc(String emailStatus);
}
