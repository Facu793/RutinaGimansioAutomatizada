package com.gestionrutina.plantilla.dto;

import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import com.gestionrutina.rutina.entity.UnidadDistancia;

import java.math.BigDecimal;

public record EjercicioPlantillaResponse(
        Long id,
        int orden,
        Long ejercicioId,
        String ejercicioNombre,
        String categoriaNombre,
        boolean ejercicioActivo,
        ModalidadTrabajo modalidad,
        Integer repeticiones,
        Integer duracionSegundos,
        BigDecimal distancia,
        UnidadDistancia unidadDistancia,
        BigDecimal porcentajeRm
) {
}
