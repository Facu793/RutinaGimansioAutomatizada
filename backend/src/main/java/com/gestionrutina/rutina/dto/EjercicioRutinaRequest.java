package com.gestionrutina.rutina.dto;

import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import com.gestionrutina.rutina.entity.UnidadDistancia;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EjercicioRutinaRequest(
        @NotNull(message = "El ejercicio es obligatorio")
        Long ejercicioId,

        @NotNull(message = "La modalidad es obligatoria")
        ModalidadTrabajo modalidad,

        Integer repeticiones,
        Integer duracionSegundos,
        BigDecimal distancia,
        UnidadDistancia unidadDistancia,
        BigDecimal porcentajeRm
) {
}
