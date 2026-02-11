package com.mma.gestion.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mma.gestion.Role;
import com.mma.gestion.dto.AuthResponseDTO;
import com.mma.gestion.dto.LoginRequestDTO;
import com.mma.gestion.dto.UserRegistrationDTO;
import com.mma.gestion.entity.Gym;
import com.mma.gestion.entity.User;
import com.mma.gestion.repository.GymRepository;
import com.mma.gestion.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(UserRegistrationDTO dto) {
        // 1. Validar si el usuario ya existe
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // 2. Buscar el gimnasio
        Gym gym = gymRepository.findById(dto.getGymId())
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado"));

        // 3. Crear el usuario y setear datos
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // ENCRIPTAR
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        user.setGym(gym); // Vinculamos al gimnasio

        userRepository.save(user);
        
        return "Usuario registrado exitosamente para el gimnasio: " + gym.getName();
    }

    // Si el usuario y contraseña son correctos, devuelve un token JWT
    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Buscamos al usuario en la DB
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificamos la contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generamos el token
        String token = jwtService.generateToken(user);

        // 4. Construimos la respuesta con TODOS los datos
        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name()) // Extraemos el nombre del Enum (ej: "ROLE_ADMIN")
                .build();
    }

}
