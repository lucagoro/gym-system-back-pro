package com.mma.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class ChartDataDTO {
    private String name; // Ejemplo: "Ene"
    private Long total;  // Cantidad de registros
    
}
