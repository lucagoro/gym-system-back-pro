package com.mma.gestion.service;

import org.springframework.stereotype.Service;

import com.mma.gestion.entity.Gym;
import com.mma.gestion.entity.User;
import com.mma.gestion.repository.GymRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GymService {
    private final GymRepository gymRepository;

    public Gym saveGym(Gym gym) {
        return gymRepository.save(gym);
    }

    public Gym getMyGym(User user) {
        // Si el usuario ya tiene el gimnasio cargado (Eager load), lo devolvemos.
        // Si no, lo buscamos asegurando que sea el suyo.
        return user.getGym();
}

    public void deleteGym(Long id, User user) {
        // Validamos que el ID que quiere borrar sea el de SU gimnasio
        if (!user.getGym().getId().equals(id)) {
            throw new RuntimeException("No tienes permisos para eliminar este gimnasio");
        }
        gymRepository.deleteById(id);
    }

}
