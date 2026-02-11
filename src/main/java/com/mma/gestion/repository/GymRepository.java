package com.mma.gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mma.gestion.entity.Gym;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    
}
