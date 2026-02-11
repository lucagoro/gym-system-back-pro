package com.mma.gestion.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.dto.ExpenseDTO;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAll(@AuthenticationPrincipal User user, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getAllExpenses(user.getGym().getId(), startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getById(id, user.getGym().getId()));
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> create(@RequestBody ExpenseDTO dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.createExpense(dto, user.getGym().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> update(@PathVariable Long id, 
                                           @RequestBody ExpenseDTO dto, 
                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.updateExpense(id, dto, user.getGym().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        expenseService.deleteExpense(id, user.getGym().getId());
        return ResponseEntity.noContent().build();
    }
}
