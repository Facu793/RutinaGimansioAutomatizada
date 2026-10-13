package com.gestionrutina.alumno.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TelefonoValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefonoValido {

    String message() default "El teléfono debe incluir entre 8 y 15 dígitos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
