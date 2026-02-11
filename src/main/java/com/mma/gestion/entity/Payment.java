package com.mma.gestion.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mma.gestion.PaymentMethod;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount; // Monto del pago

    private LocalDate paymentDate; // Fecha de pago

    private LocalDate dueDate; // Fecha de vencimiento 

    private String period; // Ej: 2025-03

    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // CASH, TRANSFER, CARD

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private Student student; // Estudiante asociado al pago - muchos pagos pueden pertenecer a un estudiante

}
