package com.gestionrutina.ejercicio.dto;

public record EjercicioResponse(
        Long id,
        String nombre,
        CategoriaEjercicioResponse categoria,
        boolean activo
) {
}
