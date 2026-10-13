package com.gestionrutina.rutina.service;

import com.gestionrutina.common.exception.PeticionInvalidaException;
import com.gestionrutina.rutina.dto.EjercicioRutinaRequest;
import java.math.BigDecimal;

public final class EjercicioRutinaReglas {

    private static final BigDecimal PORCENTAJE_MINIMO = BigDecimal.ONE;
    private static final BigDecimal PORCENTAJE_MAXIMO = new BigDecimal("100");
    private static final BigDecimal DISTANCIA_MAXIMA = new BigDecimal("9999.99");

    private EjercicioRutinaReglas() {
    }

    public static void validar(String modulo, int posicion, EjercicioRutinaRequest request) {
        String contexto = modulo + ", ejercicio " + posicion;
        switch (request.modalidad()) {
            case REPETICIONES -> validarRepeticiones(contexto, request);
            case TIEMPO -> validarTiempo(contexto, request);
            case DISTANCIA -> validarDistancia(contexto, request);
        }
    }

    private static void validarRepeticiones(String contexto, EjercicioRutinaRequest request) {
        if (request.repeticiones() == null) {
            throw new PeticionInvalidaException(contexto + ": las repeticiones son obligatorias");
        }
        if (request.repeticiones() < 1 || request.repeticiones() > 999) {
            throw new PeticionInvalidaException(contexto + ": las repeticiones deben estar entre 1 y 999");
        }
        validarPorcentaje(contexto, request.porcentajeRm());
        if (request.duracionSegundos() != null || request.distancia() != null || request.unidadDistancia() != null) {
            throw new PeticionInvalidaException(contexto + ": el tiempo y la distancia no corresponden a repeticiones");
        }
    }

    private static void validarTiempo(String contexto, EjercicioRutinaRequest request) {
        if (request.duracionSegundos() == null) {
            throw new PeticionInvalidaException(contexto + ": la duración en segundos es obligatoria");
        }
        if (request.duracionSegundos() < 1 || request.duracionSegundos() > 86400) {
            throw new PeticionInvalidaException(contexto + ": la duración debe estar entre 1 segundo y 24 horas");
        }
        if (request.repeticiones() != null || request.porcentajeRm() != null
                || request.distancia() != null || request.unidadDistancia() != null) {
            throw new PeticionInvalidaException(contexto + ": las repeticiones, el porcentaje y la distancia no corresponden al tiempo");
        }
    }

    private static void validarDistancia(String contexto, EjercicioRutinaRequest request) {
        if (request.distancia() == null || request.unidadDistancia() == null) {
            throw new PeticionInvalidaException(contexto + ": la distancia y su unidad son obligatorias");
        }
        if (request.distancia().compareTo(BigDecimal.ZERO) <= 0
                || request.distancia().compareTo(DISTANCIA_MAXIMA) > 0
                || request.distancia().stripTrailingZeros().scale() > 2) {
            throw new PeticionInvalidaException(contexto + ": la distancia debe ser mayor a 0, de hasta 9999.99 y con 2 decimales");
        }
        if (request.repeticiones() != null || request.porcentajeRm() != null || request.duracionSegundos() != null) {
            throw new PeticionInvalidaException(contexto + ": las repeticiones, el porcentaje y el tiempo no corresponden a la distancia");
        }
    }

    private static void validarPorcentaje(String contexto, BigDecimal porcentaje) {
        if (porcentaje == null) {
            return;
        }
        if (porcentaje.stripTrailingZeros().scale() > 1
                || porcentaje.compareTo(PORCENTAJE_MINIMO) < 0
                || porcentaje.compareTo(PORCENTAJE_MAXIMO) > 0) {
            throw new PeticionInvalidaException(contexto + ": el porcentaje debe estar entre 1 y 100, con hasta 1 decimal");
        }
    }
}
