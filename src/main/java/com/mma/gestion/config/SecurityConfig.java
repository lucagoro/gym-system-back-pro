package com.mma.gestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Esto le dice: "Usá mi CorsConfig"
            .csrf(csrf -> csrf.disable()) // Lo desactivamos porque JWT no lo necesita
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/gyms").permitAll()

                .requestMatchers("/students/stats/**").hasRole("ADMIN")
                .requestMatchers("/students/summary").hasRole("ADMIN") // Solo el dueño ve el resumen

                .requestMatchers("/finances/**").hasRole("ADMIN") // Solo el dueño ve la plata
                .requestMatchers("/expenses/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/plans/**").hasAnyRole("ADMIN", "USER") // Ambos ven
                .requestMatchers("/plans/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/students/**").hasRole("ADMIN") // Solo el dueño borra
                .requestMatchers("/students/**").hasAnyRole("ADMIN", "USER") // Ambos ven y crean alumnos

                .anyRequest().authenticated() // Todo lo demás requiere estar logueado
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No guardamos sesiones en el servidor
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

}
