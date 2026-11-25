package com.vitalink.platform.care.infrastructure.acl;

import com.vitalink.platform.care.domain.ports.NotificationRecipientDirectory;
import com.vitalink.platform.iam.infrastructure.persistence.jpa.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class IamNotificationRecipientAdapter implements NotificationRecipientDirectory {
  private final UserRepository users;

  public IamNotificationRecipientAdapter(UserRepository users) { this.users = users; }

  public Optional<String> emailFor(String id) {
    return users.findById(id).map(user -> user.getEmail());
  }
}
