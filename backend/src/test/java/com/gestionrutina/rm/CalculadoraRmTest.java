package com.gestionrutina.rm;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraRmTest {

    @Test
    void ochentaPorCientoDeCienKilosEsOchenta() {
        assertEquals(new BigDecimal("80.00"), CalculadoraRm.pesoRecomendado(new BigDecimal("100"), new BigDecimal("80")));
    }

    @Test
    void setentaYCincoPorCientoDeCientoCuarentaEsCientoCinco() {
        assertEquals(new BigDecimal("105.00"), CalculadoraRm.pesoRecomendado(new BigDecimal("140.00"), new BigDecimal("75")));
    }

    @Test
    void porcentajeConUnDecimal() {
        assertEquals(new BigDecimal("82.50"), CalculadoraRm.pesoRecomendado(new BigDecimal("100"), new BigDecimal("82.5")));
    }
}
