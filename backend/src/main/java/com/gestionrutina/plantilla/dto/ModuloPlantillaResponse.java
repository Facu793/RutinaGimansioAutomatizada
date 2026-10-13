package com.gestionrutina.plantilla.dto;

import java.util.List;

public record ModuloPlantillaResponse(
        Long id,
        String nombre,
        int orden,
        int rondas,
        int descansoEjercicioSegundos,
        int descansoRondaSegundos,
        List<EjercicioPlantillaResponse> ejercicios
) {
}
