package com.gestionrutina.rutina.dto;

import java.time.LocalDate;

public record RutinaResumenResponse(
        Long id,
        Long alumnoId,
        String nombre,
        String objetivo,
        LocalDate fechaCreacion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean activa
) {
}
