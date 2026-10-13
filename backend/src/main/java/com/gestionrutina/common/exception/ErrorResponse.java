package com.gestionrutina.common.exception;

import java.util.Map;

public record ErrorResponse(String mensaje, Map<String, String> errores) {
}
