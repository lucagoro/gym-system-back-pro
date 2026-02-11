package com.mma.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mma.gestion.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByGymId(Long gymId); // Siempre filtrar por gimnasio
    
}
