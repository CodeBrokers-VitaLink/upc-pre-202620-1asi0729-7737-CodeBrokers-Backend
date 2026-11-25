package com.vitalink.platform.care;
import static org.junit.jupiter.api.Assertions.*;
import com.vitalink.platform.care.domain.model.aggregates.*;
import com.vitalink.platform.care.infrastructure.persistence.jpa.*;
import com.vitalink.platform.iam.domain.model.aggregates.User;
import com.vitalink.platform.iam.domain.model.valueobjects.Role;
import com.vitalink.platform.iam.infrastructure.persistence.jpa.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.net.URI;
import java.net.http.*;
import java.util.Set;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
 "spring.datasource.url=jdbc:h2:mem:observations;MODE=MySQL;DB_CLOSE_DELAY=-1",
 "spring.datasource.username=sa","spring.datasource.password=",
 "spring.jpa.hibernate.ddl-auto=create-drop","app.demo-data=true",
 "RESEND_API_KEY=","MAIL_FROM="})
@ActiveProfiles("test")
class ObservationFlowTest {
 @LocalServerPort int port;
 @Autowired UserRepository users;
 @Autowired PatientRepository patients;
 @Autowired CareAlertRepository alerts;
 @Autowired PasswordEncoder passwords;
 private final HttpClient client=HttpClient.newHttpClient();
 private final tools.jackson.databind.json.JsonMapper json=tools.jackson.databind.json.JsonMapper.builder().build();
 private HttpResponse<String> call(String method,String path,String token,String body) throws Exception {
  var request=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/v1"+path))
   .header("Content-Type","application/json");
  if(token!=null) request.header("Authorization","Bearer "+token);
  return client.send(request.method(method,HttpRequest.BodyPublishers.ofString(body==null?"":body)).build(),HttpResponse.BodyHandlers.ofString());
 }
 private String login(String email) throws Exception {
  var r=call("POST","/auth/sign-in",null,json.writeValueAsString(java.util.Map.of("email",email,"password","Demo12345")));
  assertEquals(200,r.statusCode(),r.body());return json.readTree(r.body()).get("token").asText();
 }
 @Test void observationNotifiesOnlyRecipientsAndClosesReview() throws Exception {
  users.save(new User("PT-TEST","Paciente","patient@test.demo",passwords.encode("Demo12345"),Role.PATIENT,"900000000"));
  patients.save(new Patient("P-TEST","Paciente","Prueba",30,"Lima","DR-01","PT-TEST",Set.of("FC-01")));
  alerts.save(new CareAlert("A-TEST","P-TEST","help",com.vitalink.platform.care.domain.model.valueobjects.Severity.HIGH,null));
  var doctor=login("ana@vitalink.demo");var patient=login("patient@test.demo");var family=login("maria@vitalink.demo");
  var other=login("luis@vitalink.demo");
  assertEquals(403,call("POST","/patients/P-TEST/observations",patient,json.writeValueAsString(java.util.Map.of("text","No permitido"))).statusCode());
  assertEquals(404,call("GET","/patients/P-TEST/observations",other,null).statusCode());
  assertEquals(400,call("POST","/patients/P-TEST/observations",doctor,json.writeValueAsString(java.util.Map.of("text","   "))).statusCode());
  var created=call("POST","/patients/P-TEST/observations",doctor,json.writeValueAsString(java.util.Map.of("text","Revisión completada. Seguir indicaciones.")));
  assertEquals(201,created.statusCode(),created.body());
  var observation=json.readTree(created.body());var id=observation.get("id").asText();
  assertTrue(call("GET","/patients/P-TEST/observations",patient,null).body().contains("Seguir indicaciones."));
  var notices=json.readTree(call("GET","/notifications",patient,null).body());
  assertEquals(1,notices.size());var noticeId=notices.get(0).get("id").asText();
  assertTrue(notices.get(0).get("readAt").isNull());
  assertEquals("PENDING",notices.get(0).get("emailStatus").asText());
  assertEquals(1,json.readTree(call("GET","/notifications",family,null).body()).size());
  assertEquals(0,json.readTree(call("GET","/notifications",other,null).body()).size());
  assertEquals(404,call("PATCH","/notifications/"+noticeId+"/read",other,"{}").statusCode());
  assertEquals(200,call("PATCH","/notifications/"+noticeId+"/read",patient,"{}").statusCode());
  assertFalse(json.readTree(call("GET","/notifications",patient,null).body()).get(0).get("readAt").isNull());
  var attended=call("POST","/patients/P-TEST/observations/"+id+"/attend",doctor,"{}");
  assertEquals(200,attended.statusCode(),attended.body());
  assertFalse(json.readTree(attended.body()).get("attendedAt").isNull());
  assertEquals(com.vitalink.platform.care.domain.model.valueobjects.AlertStatus.ATTENDED,alerts.findById("A-TEST").orElseThrow().getStatus());
  assertEquals(409,call("POST","/patients/P-TEST/observations/"+id+"/attend",doctor,"{}").statusCode());
 }

 @Test void fakeRegistryValidatesDoctorSignupWithoutAuthentication() throws Exception {
  var catalog=call("GET","/medical-registry",null,null);
  assertEquals(200,catalog.statusCode(),catalog.body());
  assertEquals(5,json.readTree(catalog.body()).size());
  var lookup=call("GET","/medical-registry/70001",null,null);
  assertEquals(200,lookup.statusCode());
  assertEquals("Valeria Mendoza Castro",json.readTree(lookup.body()).get("name").asText());
  var missing=call("GET","/medical-registry/99999",null,null);
  assertEquals(400,missing.statusCode());
  assertTrue(missing.body().contains("El número de colegiatura no es válido."));
  var body=new java.util.HashMap<String,Object>();
  body.put("name","Valeria Mendoza Castro");body.put("email","valeria@registry.demo");
  body.put("password","Demo12345");body.put("role","doctor");body.put("phone","900000123");
  body.put("specialty","Medicina general");body.put("practiceType","independent");body.put("professionalLicense","99999");
  var invalid=call("POST","/auth/sign-up",null,json.writeValueAsString(body));
  assertEquals(400,invalid.statusCode(),invalid.body());
  assertTrue(invalid.body().contains("El número de colegiatura no es válido."));
  assertFalse(users.existsByEmail("valeria@registry.demo"));
  body.put("professionalLicense","70001");body.put("name","Nombre Incorrecto");
  assertEquals(400,call("POST","/auth/sign-up",null,json.writeValueAsString(body)).statusCode());
  body.put("name","  VALERIA   MENDOZA CASTRO  ");
  var valid=call("POST","/auth/sign-up",null,json.writeValueAsString(body));
  assertEquals(201,valid.statusCode(),valid.body());
  assertTrue(users.existsByEmail("valeria@registry.demo"));
  body.put("email","otra-valeria@registry.demo");
  assertEquals(409,call("POST","/auth/sign-up",null,json.writeValueAsString(body)).statusCode());
 }
}
