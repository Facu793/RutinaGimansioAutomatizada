package com.gestionrutina.rm.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AlumnoEjercicioRmResponse(
        Long id,
        Long alumnoId,
        Long ejercicioId,
        String ejercicioNombre,
        String categoriaNombre,
        BigDecimal peso,
        Instant actualizadoEn
) {
}
