package com.gestionrutina.ejercicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EjercicioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre admite hasta 120 caracteres")
        String nombre,

        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId
) {

    public EjercicioRequest {
        nombre = limpiar(nombre);
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim().replaceAll("\\s+", " ");
    }
}
