package com.gestionrutina.rutina.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ModuloRutinaRequest(
        @NotBlank(message = "El nombre del módulo es obligatorio")
        @Size(max = 80, message = "El nombre del módulo admite hasta 80 caracteres")
        String nombre,

        @NotNull(message = "La cantidad de rondas es obligatoria")
        @Min(value = 1, message = "El módulo debe tener al menos 1 ronda")
        @Max(value = 99, message = "El módulo admite hasta 99 rondas")
        Integer rondas,

        @NotNull(message = "El descanso entre ejercicios es obligatorio")
        @Min(value = 0, message = "El descanso entre ejercicios no puede ser negativo")
        @Max(value = 7200, message = "El descanso entre ejercicios admite hasta 7200 segundos")
        Integer descansoEjercicioSegundos,

        @NotNull(message = "El descanso entre rondas es obligatorio")
        @Min(value = 0, message = "El descanso entre rondas no puede ser negativo")
        @Max(value = 7200, message = "El descanso entre rondas admite hasta 7200 segundos")
        Integer descansoRondaSegundos,

        @NotEmpty(message = "Cada módulo debe tener al menos un ejercicio")
        @Size(max = 40, message = "Cada módulo admite hasta 40 ejercicios")
        @Valid
        List<EjercicioRutinaRequest> ejercicios
) {

    public ModuloRutinaRequest {
        nombre = nombre == null ? null : nombre.trim().replaceAll("\\s+", " ");
    }
}
