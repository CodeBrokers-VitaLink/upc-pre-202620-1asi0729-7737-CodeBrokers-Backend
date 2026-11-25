package com.vitalink.platform.care.application.internal.commandservices;
import com.vitalink.platform.care.infrastructure.persistence.jpa.ObservationNotificationRepository;
import com.vitalink.platform.iam.infrastructure.persistence.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.util.Map;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
@Configuration @EnableScheduling
class ObservationScheduling {}
@Component
public class ObservationEmailWorker {
 private final ObservationNotificationRepository notifications;
 private final UserRepository users;
 private final String key,from;
 private final RestClient client;
 public ObservationEmailWorker(ObservationNotificationRepository n,UserRepository u,
 @Value("${RESEND_API_KEY:}") String key,@Value("${MAIL_FROM:}") String from) {
  notifications=n; users=u; this.key=key; this.from=from;
  var factory=new SimpleClientHttpRequestFactory(); factory.setConnectTimeout(5000); factory.setReadTimeout(10000);
  client=RestClient.builder().baseUrl("https://api.resend.com").requestFactory(factory).build();
 }
 @Scheduled(fixedDelayString="${app.observation-email-delay:60000}") @Transactional
 public void deliver() {
  if(key.isBlank() || from.isBlank()) return;
  for(var n:notifications.findTop20ByEmailStatusOrderByCreatedAtAsc("PENDING")) {
   var u=users.findById(n.getRecipientId());
   if(u.isEmpty()) { n.emailAttempt(false); continue; }
   try {
    var date=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Lima")).format(n.getCreatedAt());
    client.post().uri("/emails").header("Authorization","Bearer "+key)
     .header("Idempotency-Key","observation-"+n.getId())
     .body(Map.of("from",from,"to",java.util.List.of(u.get().getEmail()),
      "subject","VitaLink: nueva observación médica",
      "text","El doctor "+n.getDoctorName()+" registró una observación el "+date+" (hora de Lima). Ingresa a VitaLink para consultarla."))
     .retrieve().toBodilessEntity();
    n.emailAttempt(true);
   } catch(Exception ex) { n.emailAttempt(false); }
  }
 }
}
