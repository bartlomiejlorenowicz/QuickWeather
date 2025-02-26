package com.quickweather.config;

import com.quickweather.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Spring Security configuration for the application.
 * <p>
 * - Disables CSRF since we use JWT for security.
 * - Defines public endpoints vs. endpoints that require authentication.
 * - Adds the {@link JwtFilter} to the security filter chain for token validation.
 * <p>
 * This setup enables stateless authentication using JWT.
 */
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * The main Security filter chain configuration.
     *
     * @param http HttpSecurity object for building web based security.
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Configure CORS explicitly
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 2) Disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 3) Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/v1/user/auth/login",
                                "/api/v1/user/register",
                                "/api/v1/user/auth/validate-reset-token",
                                "/api/v1/user/auth/reset-password",
                                "/api/v1/user/auth/set-new-password",
                                "/api/v1/weather/city",
                                "/api/v1/weather/forecast",
                                "/api/v1/weather/zipcode",
                                "/api/v1/weather/coordinate",
                                "/api/v1/weather/forecast/daily",
                                "/api/v1/user/auth/forgot-password",
                                "/api/v1/weather/city/air-quality",
                                "/api/v1/history"
                        ).permitAll()
                        .requestMatchers("/api/v1/history/current-with-user-history").authenticated()
                        .requestMatchers("/api/v1/user/auth/change-password").authenticated()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                // 4) Use stateless sessions (JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5) Add our custom JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 6) Handle authentication exceptions (401)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()));

        // Build and return the configured filter chain
        return http.build();
    }

    /**
     * Defines how to handle unauthorized requests (returns HTTP 401).
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Provides a password encoder (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager as a bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**");
    }


}
