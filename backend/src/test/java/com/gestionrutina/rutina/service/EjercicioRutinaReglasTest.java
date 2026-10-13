package com.gestionrutina.rutina.service;

import com.gestionrutina.common.exception.PeticionInvalidaException;
import com.gestionrutina.rutina.dto.EjercicioRutinaRequest;
import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import com.gestionrutina.rutina.entity.UnidadDistancia;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EjercicioRutinaReglasTest {

    @Test
    void repeticionesConPorcentajeEsValido() {
        assertDoesNotThrow(() -> EjercicioRutinaReglas.validar(
                "Módulo 1",
                1,
                new EjercicioRutinaRequest(4L, ModalidadTrabajo.REPETICIONES, 8, null, null, null, new BigDecimal("80"))
        ));
    }

    @Test
    void tiempoRechazaRepeticionesYPorcentaje() {
        assertThrows(PeticionInvalidaException.class, () -> EjercicioRutinaReglas.validar(
                "Módulo 2",
                1,
                new EjercicioRutinaRequest(5L, ModalidadTrabajo.TIEMPO, 8, 45, null, null, new BigDecimal("80"))
        ));
    }

    @Test
    void distanciaExigeUnidad() {
        assertThrows(PeticionInvalidaException.class, () -> EjercicioRutinaReglas.validar(
                "Módulo 2",
                3,
                new EjercicioRutinaRequest(6L, ModalidadTrabajo.DISTANCIA, null, null, new BigDecimal("1"), null, null)
        ));
    }

    @Test
    void unKilometroEsValido() {
        assertDoesNotThrow(() -> EjercicioRutinaReglas.validar(
                "Módulo 2",
                3,
                new EjercicioRutinaRequest(6L, ModalidadTrabajo.DISTANCIA, null, null, new BigDecimal("1"), UnidadDistancia.KILOMETROS, null)
        ));
    }
}
