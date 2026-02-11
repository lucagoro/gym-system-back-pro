package com.mma.gestion.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mma.gestion.dto.ExpenseDTO;
import com.mma.gestion.entity.Expense;
import com.mma.gestion.repository.ExpenseRepository;
import com.mma.gestion.repository.GymRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GymRepository gymRepository;

    public List<ExpenseDTO> getAllExpenses(Long gymId, LocalDate startDate, LocalDate endDate) {
    // Aplicamos la misma lógica por defecto que en el Balance
    LocalDate finalStart = (startDate != null) ? startDate : LocalDate.now().withDayOfMonth(1);
    LocalDate finalEnd = (endDate != null) ? endDate : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

    return expenseRepository.findByGymIdAndExpenseDateBetweenOrderByExpenseDateDesc(gymId, finalStart, finalEnd)
            .stream()
            .map(this::convertToDTO)
            .toList();
}

    public ExpenseDTO getById(Long id, Long gymId) {
        return expenseRepository.findById(id)
            .filter(e -> e.getGym().getId().equals(gymId))
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));
    }

    public ExpenseDTO createExpense(ExpenseDTO dto, Long gymId) {
        Expense expense = new Expense();
        expense.setDescription(dto.description());
        expense.setAmount(dto.amount());
        expense.setExpenseDate(dto.expenseDate() != null ? dto.expenseDate() : LocalDate.now());
        expense.setCategory(dto.category());
        
        // Vinculamos al gimnasio por ID (Referencia rápida)
        expense.setGym(gymRepository.getReferenceById(gymId));

        Expense saved = expenseRepository.save(expense);
        return convertToDTO(saved);
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO dto, Long gymId) {
        Expense expense = expenseRepository.findById(id)
                .filter(e -> e.getGym().getId().equals(gymId))
                .orElseThrow(() -> new RuntimeException("No autorizado"));

        expense.setDescription(dto.description());
        expense.setAmount(dto.amount());
        expense.setExpenseDate(dto.expenseDate());
        expense.setCategory(dto.category());

        return convertToDTO(expenseRepository.save(expense));
    }

    public void deleteExpense(Long id, Long gymId) {
        Expense expense = expenseRepository.findById(id)
                .filter(e -> e.getGym().getId().equals(gymId))
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado o no autorizado"));
        expenseRepository.delete(expense);
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getCategory()
        );
    }
}
