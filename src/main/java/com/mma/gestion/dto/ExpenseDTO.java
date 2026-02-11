package com.mma.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDTO(
    Long id,
    String description,
    BigDecimal amount,
    LocalDate expenseDate,
    String category
) {}
