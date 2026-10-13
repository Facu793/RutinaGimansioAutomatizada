package com.gestionrutina.alumno.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Clock;
import java.time.LocalDate;

public class FechaNacimientoValidator implements ConstraintValidator<FechaNacimientoValida, LocalDate> {

    private final Clock clock;

    public FechaNacimientoValidator(Clock clock) {
        this.clock = clock;
    }

    @Override
    public boolean isValid(LocalDate fecha, ConstraintValidatorContext context) {
        if (fecha == null) {
            return true;
        }
        LocalDate hoy = LocalDate.now(clock);
        return !fecha.isBefore(LocalDate.of(1900, 1, 1)) && !fecha.isAfter(hoy);
    }
}
