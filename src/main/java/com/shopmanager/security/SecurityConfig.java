package com.shopmanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Password Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔐 Main Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);

        http
                // ❌ Disable CSRF (Required for REST APIs)
                .csrf(csrf -> csrf.disable())

                // 🌐 Enable CORS
                .cors(Customizer.withDefaults())

                // 🔒 Stateless session (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC LOGIN (VERY IMPORTANT)
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/repairs/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/repairs/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/repairs/**").authenticated()
                        .requestMatchers("/api/customers/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/mobile-sales/**").authenticated()
                        // Mobile Sales
                        .requestMatchers(HttpMethod.POST, "/api/mobile-sales/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/mobile-sales/**").authenticated()






                        // Swagger / API Docs
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Dev / H2
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // 🔒 All other APIs need login
                        .anyRequest().authenticated()
                )

                // ❌ Disable default Spring login UI
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                // 🔑 Add JWT Filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Allow H2 console frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}