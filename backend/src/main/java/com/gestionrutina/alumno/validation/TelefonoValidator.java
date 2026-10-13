package com.gestionrutina.alumno.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefonoValidator implements ConstraintValidator<TelefonoValido, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true;
        }
        if (!valor.matches("^[0-9+()\\-\\s]+$")) {
            return false;
        }
        long digitos = valor.chars().filter(Character::isDigit).count();
        return digitos >= 8 && digitos <= 15;
    }
}
