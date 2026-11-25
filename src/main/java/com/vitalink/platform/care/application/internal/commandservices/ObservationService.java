package com.vitalink.platform.care.application.internal.commandservices;
import com.vitalink.platform.care.application.queryservices.CareAccessService;
import com.vitalink.platform.care.application.commandservices.CareCommandService;
import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.domain.model.commands.ChangeAlertStatusCommand;
import com.vitalink.platform.care.domain.model.valueobjects.AlertStatus;
import com.vitalink.platform.care.infrastructure.persistence.jpa.*;
import com.vitalink.platform.iam.application.queryservices.CurrentUserService;
import com.vitalink.platform.shared.domain.model.exceptions.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional
public class ObservationService {
 private final DoctorObservationRepository observations;
 private final ObservationNotificationRepository notifications;
 private final CareAccessService access;
 private final CurrentUserService current;
 private final PatientRepository patients;
 private final CareAlertRepository alerts;
 private final CareCommandService commands;
 public ObservationService(DoctorObservationRepository o, ObservationNotificationRepository n,
 CareAccessService a, CurrentUserService c, PatientRepository p, CareAlertRepository alerts, CareCommandService commands) {
  observations=o; notifications=n; access=a; current=c; patients=p; this.alerts=alerts; this.commands=commands;
 }
 public List<DoctorObservation> list(String id) { access.requirePatient(id); return observations.findAllByPatientIdOrderByCreatedAtDesc(id); }
 public DoctorObservation create(String id,String text) {
  access.requireDoctor(); access.requirePatient(id);
  BusinessException.require(text!=null && !text.isBlank() && text.length()<=2000,400,"Escribe una observación de hasta 2000 caracteres.");
  var p=patients.findForUpdate(id).orElseThrow();
  var u=current.require();
  var o=observations.save(new DoctorObservation(id,u.getId(),u.getName(),text.trim()));
  var recipients=new HashSet<>(p.getFamilyIds());
  if(p.getUserId()!=null) recipients.add(p.getUserId());
  recipients.forEach(r->notifications.save(new ObservationNotification(r,o)));
  return o;
 }
 public DoctorObservation attend(String id,String observationId) {
  access.requireDoctor(); access.requirePatient(id); patients.findForUpdate(id).orElseThrow();
  var o=observations.findById(observationId).orElseThrow(()->new BusinessException(404,"Observación no encontrada."));
  BusinessException.require(o.getPatientId().equals(id) && o.getDoctorId().equals(current.require().getId()),403,"Observación no autorizada.");
  BusinessException.require(o.getAttendedAt()==null,409,"Esta revisión ya fue atendida.");
  var latest=observations.findAllByPatientIdOrderByCreatedAtDesc(id);
  BusinessException.require(!latest.isEmpty() && latest.getFirst().getId().equals(o.getId()),409,"Utiliza la observación más reciente.");
  for(var a: alerts.findAllByPatientId(id)) {
   BusinessException.require(!a.getRaisedAt().isAfter(o.getCreatedAt()) || a.getStatus()==AlertStatus.ATTENDED,409,"Hay nuevas alertas. Envía otra observación.");
   if(a.getStatus()!=AlertStatus.ATTENDED)
    commands.handle(new ChangeAlertStatusCommand(a.getId(),AlertStatus.ATTENDED,o.getText(),a.getVersion()));
  }
  o.attend(); return observations.save(o);
 }
 public List<ObservationNotification> notifications() { return notifications.findAllByRecipientIdOrderByCreatedAtDesc(current.require().getId()); }
 public ObservationNotification read(String id) {
  var n=notifications.findById(id).filter(x->x.getRecipientId().equals(current.require().getId()))
   .orElseThrow(()->new BusinessException(404,"Notificación no encontrada."));
  n.read(); return notifications.save(n);
 }
}
