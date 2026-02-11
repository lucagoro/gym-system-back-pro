package com.mma.gestion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.dto.PaymentDTO;
import com.mma.gestion.dto.PaymentRequestDTO;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class PaymentByStudentController {

    private final PaymentService paymentService;

    // Registrar un pago nuevo
    @PostMapping("/{studentId}/payments")
    public ResponseEntity<PaymentDTO> createPayment(
            @PathVariable Long studentId,
            @AuthenticationPrincipal User user, @RequestBody PaymentRequestDTO request) { // Necesitamos al user para el gymId

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPaymentForStudent(studentId, user.getGym().getId(), request.planId(), request.method(), request.paymentDate()));
    }

    // Listar historial de un alumno
    @GetMapping("/{studentId}/payments")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStudent(
            @PathVariable Long studentId,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(paymentService.getPaymentsByStudentId(studentId, user.getGym().getId()));
    }

}

