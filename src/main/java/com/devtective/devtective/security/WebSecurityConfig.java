package com.devtective.devtective.security;

import com.devtective.devtective.service.auth.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebSecurity()
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
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

                //.authorizeHttpRequests(requests -> requests
                //        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                //        .requestMatchers("/", "/home", "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                //        .requestMatchers(HttpMethod.POST, "/api/v1/projects/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                //        .anyRequest().authenticated()
                //)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new AuthorizationService(); // Assuming this is your implementation
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return new ProviderManager(authProvider);
//    }


//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}
