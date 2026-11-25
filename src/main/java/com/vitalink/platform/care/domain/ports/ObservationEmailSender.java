package com.vitalink.platform.care.domain.ports;

import com.vitalink.platform.care.domain.model.valueobjects.ObservationEmail;

public interface ObservationEmailSender {
  boolean isConfigured();
  void send(ObservationEmail email);
}
