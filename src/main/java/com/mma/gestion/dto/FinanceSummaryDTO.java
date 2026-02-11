package com.mma.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceSummaryDTO(
    BigDecimal totalIncomes,
    BigDecimal totalExpenses,
    BigDecimal netProfit,
    LocalDate startDate,
    LocalDate endDate
) {}
