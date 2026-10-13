package com.gestionrutina.rm.dto;

import java.math.BigDecimal;

public record CalculoRmResponse(
        Long ejercicioId,
        String ejercicioNombre,
        BigDecimal porcentaje,
        BigDecimal rm,
        BigDecimal pesoCalculado,
        String advertencia
) {
}
