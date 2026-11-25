package com.vitalink.platform.care.domain.ports;

import java.util.Optional;

public interface NotificationRecipientDirectory {
  Optional<String> emailFor(String recipientId);
}
