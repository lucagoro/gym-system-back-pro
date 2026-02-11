package com.mma.gestion.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.dto.FinanceSummaryDTO;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {
    private final FinanceService financeService;

    @GetMapping("/balance")
    public ResponseEntity<FinanceSummaryDTO> getBalance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @AuthenticationPrincipal User user) {
        
        return ResponseEntity.ok(financeService.getBalance(user.getGym().getId(), start, end));
    }
}
