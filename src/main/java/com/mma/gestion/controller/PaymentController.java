package com.mma.gestion.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.dto.PaymentDTO;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Listar TODOS los pagos del gimnasio (Dashboard/Historial general)
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getAllPayments(user.getGym().getId()));
    }

    @GetMapping("/report")
    public ResponseEntity<List<PaymentDTO>> getPaymentsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user) {
        
        return ResponseEntity.ok(paymentService.getPaymentsByDate(startDate, endDate, user.getGym().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @PathVariable Long id, 
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPaymentById(id, user.getGym().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentDTO paymentDTO,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(paymentService.updatePayment(id, paymentDTO, user.getGym().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long id, 
            @AuthenticationPrincipal User user) {
        paymentService.deletePayment(id, user.getGym().getId());
        return ResponseEntity.noContent().build();
    }
}
