package com.mma.gestion.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class GymDTO {
    private String name;
    private String address;
    private String phone;
    private BigDecimal monthlyFee; 
    private BigDecimal inscriptionFee; 
}
