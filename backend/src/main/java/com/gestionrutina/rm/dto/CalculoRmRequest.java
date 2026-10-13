package com.gestionrutina.rm.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalculoRmRequest(
        @NotNull(message = "El ejercicio es obligatorio")
        Long ejercicioId,

        @NotNull(message = "El porcentaje es obligatorio")
        @DecimalMin(value = "1.0", message = "El porcentaje debe estar entre 1 y 100")
        @DecimalMax(value = "100.0", message = "El porcentaje debe estar entre 1 y 100")
        @Digits(integer = 3, fraction = 1, message = "El porcentaje admite hasta 1 decimal")
        BigDecimal porcentaje
) {
}
