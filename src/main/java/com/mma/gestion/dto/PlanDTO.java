package com.mma.gestion.dto;

import java.math.BigDecimal;

public record PlanDTO(
    Long id,
    String name,
    BigDecimal price,
    int durationDays
) {}
