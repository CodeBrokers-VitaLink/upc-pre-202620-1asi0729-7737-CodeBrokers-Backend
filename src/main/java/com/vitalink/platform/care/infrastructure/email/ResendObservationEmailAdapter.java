package com.vitalink.platform.care.infrastructure.email;

import com.vitalink.platform.care.domain.model.valueobjects.ObservationEmail;
import com.vitalink.platform.care.domain.ports.ObservationEmailSender;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ResendObservationEmailAdapter implements ObservationEmailSender {
  private static final DateTimeFormatter DATE =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Lima"));
  private final String key;
  private final String from;
  private final RestClient client;

  public ResendObservationEmailAdapter(
      @Value("${RESEND_API_KEY:}") String key, @Value("${MAIL_FROM:}") String from) {
    this.key = key;
    this.from = from;
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    client = RestClient.builder().baseUrl("https://api.resend.com")
        .requestFactory(factory).build();
  }

  public boolean isConfigured() { return !key.isBlank() && !from.isBlank(); }

  public void send(ObservationEmail email) {
    client.post().uri("/emails")
        .header("Authorization", "Bearer " + key)
        .header("Idempotency-Key", "observation-" + email.notificationId())
        .body(Map.of(
            "from", from, "to", List.of(email.recipientEmail()),
            "subject", "VitaLink: nueva observación médica",
            "text", "El doctor " + email.doctorName() + " registró una observación el "
                + DATE.format(email.observedAt())
                + " (hora de Lima). Ingresa a VitaLink para consultarla."))
        .retrieve().toBodilessEntity();
  }
}
