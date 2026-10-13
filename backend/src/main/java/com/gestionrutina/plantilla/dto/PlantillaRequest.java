package com.gestionrutina.plantilla.dto;

import com.gestionrutina.rutina.dto.ModuloRutinaRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlantillaRequest(
        @NotBlank(message = "El nombre de la plantilla es obligatorio")
        @Size(max = 120, message = "El nombre de la plantilla admite hasta 120 caracteres")
        String nombre,

        @NotBlank(message = "El objetivo es obligatorio")
        @Size(max = 200, message = "El objetivo admite hasta 200 caracteres")
        String objetivo,

        @Size(max = 1000, message = "Las observaciones admiten hasta 1000 caracteres")
        String observaciones,

        @NotEmpty(message = "La plantilla debe tener al menos un módulo")
        @Size(max = 20, message = "La plantilla admite hasta 20 módulos")
        @Valid
        List<ModuloRutinaRequest> modulos
) {

    public PlantillaRequest {
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
