package lk.icbt.dentalclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Functionality 1 (User Authentication) plus the "distributed application with web
 * services" requirement (Task B.i), implemented as two independent filter chains:
 * <p>
 * 1. {@code /api/**} - stateless HTTP Basic auth, for machine clients (a partner lab
 *    system, a future mobile app, curl/Postman) consuming the REST web services.
 * 2. everything else - classic session/cookie-based form login for the staff-facing
 *    Thymeleaf web UI, matching the "menu driven application" the brief asks for.
 * <p>
 * Both chains authenticate against the same {@code staff_users} table, so there is
 * exactly one source of truth for "who is authorized staff".
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(httpBasic -> {});
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                // H2 console renders in a frame and posts its own form; both need relaxed defaults for local dev use.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }
}
