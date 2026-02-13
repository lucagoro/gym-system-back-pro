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
                // 1. Rutas públicas
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/gyms").permitAll()

                // 2. Estadísticas y Resúmenes (GET): Ahora GUEST también mira
                .requestMatchers(HttpMethod.GET, "/students/stats/**", "/students/summary").hasAnyRole("ADMIN", "GUEST")
                .requestMatchers("/students/stats/**", "/students/summary").hasRole("ADMIN")

                // 3. Finanzas y Gastos: GUEST solo puede ver (GET)
                .requestMatchers(HttpMethod.GET, "/finances/**", "/expenses/**").hasAnyRole("ADMIN", "GUEST")
                .requestMatchers("/finances/**", "/expenses/**").hasRole("ADMIN") // POST, PUT, DELETE solo ADMIN

                // 4. Planes: Todos ven (GET), pero solo ADMIN gestiona
                .requestMatchers(HttpMethod.GET, "/plans/**").hasAnyRole("ADMIN", "USER", "GUEST")
                .requestMatchers("/plans/**").hasRole("ADMIN")

                // 5. Alumnos (Students):
                // - Borrar: Solo ADMIN
                .requestMatchers(HttpMethod.DELETE, "/students/**").hasRole("ADMIN")
                // - Crear/Editar: ADMIN y USER (GUEST no puede porque es POST/PUT)
                .requestMatchers(HttpMethod.POST, "/students/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/students/**").hasAnyRole("ADMIN", "USER")
                // - Ver: Los tres roles pueden ver la lista o detalles
                .requestMatchers(HttpMethod.GET, "/students/**").hasAnyRole("ADMIN", "USER", "GUEST")

                // 6. Regla general para el resto de endpoints de alumnos (por si quedaron otros)
                .requestMatchers("/students/**").hasAnyRole("ADMIN", "USER")

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
