package com.fawwaz.transactionmonitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Two independent security policies:
 *
 * <ul>
 *   <li><b>/api/**</b> — stateless HTTP Basic (for machine-to-machine ingestion).
 *       CSRF is disabled here because there is no browser session to protect.</li>
 *   <li><b>everything else</b> — browser form login for the analyst UI, with CSRF
 *       protection enabled (the default).</li>
 * </ul>
 *
 * A single demo user ({@code analyst}) with role {@code ANALYST} guards both.
 * Credentials come from {@code app.security.*} and can be overridden per
 * environment; the password is BCrypt-hashed at startup, never stored in clear.
 */
@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ANALYST"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error").permitAll()
                        .anyRequest().hasRole("ANALYST"))
                .formLogin(form -> form.defaultSuccessUrl("/", true).permitAll())
                .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(
            @Value("${app.security.username}") String username,
            @Value("${app.security.password}") String password,
            PasswordEncoder encoder) {
        UserDetails analyst = User.withUsername(username)
                .password(encoder.encode(password))
                .roles("ANALYST")
                .build();
        return new InMemoryUserDetailsManager(analyst);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
