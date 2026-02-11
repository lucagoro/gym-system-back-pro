package com.mma.gestion.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.mma.gestion.dto.FinanceSummaryDTO;
import com.mma.gestion.repository.ExpenseRepository;
import com.mma.gestion.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

    public FinanceSummaryDTO getBalance(Long gymId, LocalDate start, LocalDate end) {
        // Lógica por defecto: Si las fechas son null, usamos el mes actual
        LocalDate finalStart = (start != null) ? start : LocalDate.now().withDayOfMonth(1);
        LocalDate finalEnd = (end != null) ? end : LocalDate.now();

       BigDecimal incomes = paymentRepository.sumPaymentsBetween(gymId, finalStart, finalEnd);
        if (incomes == null) incomes = BigDecimal.ZERO; // Evita el crash

        BigDecimal expenses = expenseRepository.sumExpensesBetween(gymId, finalStart, finalEnd);
        if (expenses == null) expenses = BigDecimal.ZERO;

        return new FinanceSummaryDTO(
            incomes,
            expenses,
            incomes.subtract(expenses),
            finalStart,
            finalEnd
        );
    }
    
}
