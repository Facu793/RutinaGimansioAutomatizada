package com.gestionrutina.common.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
        @NotNull(message = "Debe indicar si el registro está activo")
        Boolean activo
) {
}
