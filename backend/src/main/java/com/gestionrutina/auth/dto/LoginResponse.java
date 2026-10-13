package com.gestionrutina.auth.dto;

public record LoginResponse(String token, String username, String rol) {
}
