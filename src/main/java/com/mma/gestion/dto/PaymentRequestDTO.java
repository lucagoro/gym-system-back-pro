package com.mma.gestion.dto;

import java.time.LocalDate;

import com.mma.gestion.PaymentMethod;

public record PaymentRequestDTO(Long planId, PaymentMethod method, LocalDate paymentDate) {}
