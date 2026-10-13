package com.gestionrutina.rutina.dto;

import java.util.List;

public record ModuloRutinaResponse(
        Long id,
        String nombre,
        int orden,
        int rondas,
        int descansoEjercicioSegundos,
        int descansoRondaSegundos,
        List<EjercicioRutinaResponse> ejercicios
) {
}
