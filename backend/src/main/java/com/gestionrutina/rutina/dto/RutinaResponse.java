package com.gestionrutina.rutina.dto;

import java.time.LocalDate;
import java.util.List;

public record RutinaResponse(
        Long id,
        Long alumnoId,
        String alumnoNombre,
        String alumnoApellido,
        int edad,
        String nombre,
        String objetivo,
        LocalDate fechaCreacion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String observaciones,
        boolean activa,
        List<ModuloRutinaResponse> modulos,
        List<String> advertencias
) {
}
