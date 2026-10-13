package com.gestionrutina.rm;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculadoraRm {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private CalculadoraRm() {
    }

    public static BigDecimal pesoRecomendado(BigDecimal rm, BigDecimal porcentaje) {
        return rm.multiply(porcentaje).divide(CIEN, 2, RoundingMode.HALF_UP);
    }
}
