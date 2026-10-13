package com.gestionrutina.plantilla.dto;

import java.util.List;

public record PlantillaResponse(
        Long id,
        String nombre,
        String objetivo,
        String observaciones,
        boolean activa,
        List<ModuloPlantillaResponse> modulos,
        List<String> advertencias
) {
}
