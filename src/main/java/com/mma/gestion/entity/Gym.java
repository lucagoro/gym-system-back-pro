package com.mma.gestion.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;

    // Precios base para los planes
    private BigDecimal monthlyFee; // Cuota mensual estándar
    private BigDecimal inscriptionFee; // Matrícula

    // Un gimnasio tiene muchos alumnos
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();
}
