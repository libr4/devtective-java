package com.devtective.devtective.security;

import com.devtective.devtective.service.auth.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity()
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(requests -> requests
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                    .requestMatchers("/actuator/health", "/health").permitAll()

                    .requestMatchers(HttpMethod.POST,   "/api/v1/projects/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.PUT,    "/api/v1/projects/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN", "ROLE_USER")

                    .requestMatchers(HttpMethod.POST,   "/api/v1/tasks", "/api/v1/tasks/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.PUT,"/api/v1/tasks",    "/api/v1/tasks/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.DELETE,"/api/v1/tasks", "/api/v1/tasks/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")

                    .requestMatchers(HttpMethod.GET,   "/api/v1/workspaces", "/api/v1/workspaces/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.POST,   "/api/v1/workspaces", "/api/v1/workspaces/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.PUT,"/api/v1/workspaces",    "/api/v1/workspaces/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")
                    .requestMatchers(HttpMethod.DELETE,"/api/v1/workspaces", "/api/v1/workspaces/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_WORKER", "ROLE_ADMIN", "ROLE_USER")

                    .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                            res.setContentType("application/json");
                            res.getWriter().write("""
                                {"timestamp":"%s","status":401,"error":"Unauthorized","message":"Authentication required"}
                                """.formatted(java.time.LocalDateTime.now()));
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(HttpStatus.FORBIDDEN.value());
                            res.setContentType("application/json");
                            res.getWriter().write("""
                                {"timestamp":"%s","status":403,"error":"Forbidden","message":"Access is denied"}
                                """.formatted(java.time.LocalDateTime.now()));
                        })
                )

                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl, "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
