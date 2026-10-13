package com.gestionrutina.alumno.dto;

import jakarta.validation.constraints.NotNull;

public record AlumnoEstadoRequest(
        @NotNull(message = "Debe indicar si el alumno está activo")
        Boolean activo
) {
}
