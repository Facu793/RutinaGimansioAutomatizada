package com.gestionrutina.rm.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlumnoEjercicioRmRequest(
        @NotNull(message = "El ejercicio es obligatorio")
        Long ejercicioId,

        @NotNull(message = "El peso es obligatorio")
        @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
        @DecimalMax(value = "9999.99", message = "El peso máximo permitido es 9999.99 kg")
        @Digits(integer = 4, fraction = 2, message = "El peso admite hasta 2 decimales")
        BigDecimal peso
) {
}
