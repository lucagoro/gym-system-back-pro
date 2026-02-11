package com.mma.gestion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mma.gestion.dto.PlanDTO;
import com.mma.gestion.entity.Plan;
import com.mma.gestion.repository.GymRepository;
import com.mma.gestion.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final GymRepository gymRepository;

    public List<PlanDTO> findAllByGym(Long gymId) {
        return planRepository.findByGymId(gymId).stream()
                .map(p -> new PlanDTO(p.getId(), p.getName(), p.getPrice(), p.getDurationDays()))
                .toList();
    }

    public PlanDTO getById(Long id, Long gymId) {
        return planRepository.findById(id)
                .filter(p -> p.getGym().getId().equals(gymId))
                .map(p -> new PlanDTO(p.getId(), p.getName(), p.getPrice(), p.getDurationDays()))
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
    }

    public PlanDTO create(PlanDTO dto, Long gymId) {
        Plan plan = new Plan();
        plan.setName(dto.name());
        plan.setPrice(dto.price());
        plan.setDurationDays(dto.durationDays());
        plan.setGym(gymRepository.getReferenceById(gymId));
        
        Plan saved = planRepository.save(plan);
        return new PlanDTO(saved.getId(), saved.getName(), saved.getPrice(), saved.getDurationDays());
    }

    public PlanDTO update(Long id, PlanDTO dto, Long gymId) {
        Plan plan = planRepository.findById(id)
                .filter(p -> p.getGym().getId().equals(gymId))
                .orElseThrow(() -> new RuntimeException("No autorizado para editar este plan"));

        plan.setName(dto.name());
        plan.setPrice(dto.price());
        plan.setDurationDays(dto.durationDays());

        Plan updated = planRepository.save(plan);
        return new PlanDTO(updated.getId(), updated.getName(), updated.getPrice(), updated.getDurationDays());
    }

    public void delete(Long id, Long gymId) {
        Plan plan = planRepository.findById(id)
                .filter(p -> p.getGym().getId().equals(gymId))
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        planRepository.delete(plan);
    }
    
}
