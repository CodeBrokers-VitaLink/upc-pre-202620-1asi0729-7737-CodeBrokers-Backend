package com.vitalink.platform.iam.infrastructure.security;

import com.vitalink.platform.iam.infrastructure.persistence.jpa.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
public class SecurityConfiguration {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, AuthSessionRepository sessions, UserRepository users) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .cors(cors -> {})
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        "/api/v1/auth/sign-in",
                        "/api/v1/auth/sign-up",
                        "/api/v1/enrollment/families",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/clinics", "/api/v1/doctors", "/api/v1/medical-registry", "/api/v1/medical-registry/*")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            errors ->
                errors
                    .authenticationEntryPoint(
                        (req, res, error) -> {
                          res.setStatus(401);
                          res.setContentType("application/problem+json");
                          res.getWriter()
                              .write("{\"status\":401,\"detail\":\"Debes iniciar sesión.\"}");
                        })
                    .accessDeniedHandler(
                        (req, res, error) -> {
                          res.setStatus(403);
                          res.setContentType("application/problem+json");
                          res.getWriter().write("{\"status\":403,\"detail\":\"Acceso denegado.\"}");
                        }))
        .addFilterBefore(
            new BearerTokenFilter(sessions, users), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend-origin}") String origin) {
    var config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(origin));
    config.setAllowedMethods(List.of("GET", "POST", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Accept-Language"));
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
