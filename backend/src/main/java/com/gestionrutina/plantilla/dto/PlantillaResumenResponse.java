package com.gestionrutina.plantilla.dto;

public record PlantillaResumenResponse(
        Long id,
        String nombre,
        String objetivo,
        boolean activa
) {
}
