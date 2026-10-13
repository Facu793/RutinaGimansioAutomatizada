package com.gestionrutina.rutina.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record RutinaRequest(
        @NotNull(message = "El alumno es obligatorio")
        Long alumnoId,

        @NotBlank(message = "El nombre de la rutina es obligatorio")
        @Size(max = 120, message = "El nombre de la rutina admite hasta 120 caracteres")
        String nombre,

        @NotBlank(message = "El objetivo es obligatorio")
        @Size(max = 200, message = "El objetivo admite hasta 200 caracteres")
        String objetivo,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de finalización es obligatoria")
        LocalDate fechaFin,

        @Size(max = 1000, message = "Las observaciones admiten hasta 1000 caracteres")
        String observaciones,

        @NotEmpty(message = "La rutina debe tener al menos un módulo")
        @Size(max = 20, message = "La rutina admite hasta 20 módulos")
        @Valid
        List<ModuloRutinaRequest> modulos
) {

    public RutinaRequest {
        nombre = limpiar(nombre);
        objetivo = limpiar(objetivo);
        observaciones = observaciones == null || observaciones.isBlank() ? null : observaciones.trim();
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim().replaceAll("\\s+", " ");
    }
}
