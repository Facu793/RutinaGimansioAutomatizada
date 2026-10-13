package com.gestionrutina.common;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoBusqueda {

    private TextoBusqueda() {
    }

    public static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinAcentos.toLowerCase(Locale.ROOT)
                .replace("%", "")
                .replace("_", "");
    }
}
