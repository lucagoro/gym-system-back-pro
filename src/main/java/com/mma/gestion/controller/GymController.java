package com.mma.gestion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.entity.Gym;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.GymService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/gyms")
@RequiredArgsConstructor
public class GymController {
    private final GymService gymService;

    @GetMapping("/my-gym")
    public ResponseEntity<Gym> getMyGym(@AuthenticationPrincipal User user) {
        // Como el 'user' ya trae su gimnasio desde la DB/Token
        return ResponseEntity.ok(user.getGym());
    }

    @PostMapping
    public ResponseEntity<Gym> createGym(@RequestBody Gym gym) {
        Gym createdGym = gymService.saveGym(gym);
        return ResponseEntity.ok(createdGym);
    }

}
