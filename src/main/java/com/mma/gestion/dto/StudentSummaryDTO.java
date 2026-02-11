package com.mma.gestion.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentSummaryDTO {
    private long total;
    private long alDia;
    private long vencidos;
    private long sinPagos;
    private BigDecimal totalMes;
    private List<ChartDataDTO> chartData; // Nueva propiedad para los datos del gráfico
}
