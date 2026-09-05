package hyperskill.evgentrigub.antifraud;

import hyperskill.evgentrigub.antifraud.exceptions.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)                           // For modifying requests via Postman
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handles auth error
                )
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable)) // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests                     // manage access
                        // to return 400 instead of 401 for open endpoints
                        // (endpoints redirecting to the /error/** in case of error and /error/ is secured by spring security)
                        .requestMatchers("/actuator/shutdown", "/error/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                        .requestMatchers("/api/auth/role", "/api/auth/access").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                        .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.GET,
                                "/api/antifraud/history",
                                "/api/antifraud/history/*"
                        ).hasRole("SUPPORT")
                        .requestMatchers(
                                "/api/antifraud/suspicious-ip",
                                "/api/antifraud/suspicious-ip/*",
                                "/api/antifraud/stolencard",
                                "/api/antifraud/stolencard/*"
                        ).hasRole("SUPPORT")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
