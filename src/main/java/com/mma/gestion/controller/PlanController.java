package com.mma.gestion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.dto.PlanDTO;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.PlanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(planService.findAllByGym(user.getGym().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(planService.getById(id, user.getGym().getId()));
    }

    @PostMapping
    public ResponseEntity<PlanDTO> create(@RequestBody PlanDTO dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(planService.create(dto, user.getGym().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> update(@PathVariable Long id, 
                                        @RequestBody PlanDTO dto, 
                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(planService.update(id, dto, user.getGym().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        planService.delete(id, user.getGym().getId());
        return ResponseEntity.noContent().build();
    }
    
}
