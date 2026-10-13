package com.gestionrutina.alumno.dto;

import com.gestionrutina.alumno.validation.FechaNacimientoValida;
import com.gestionrutina.alumno.validation.TelefonoValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlumnoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite hasta 80 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 80, message = "El apellido admite hasta 80 caracteres")
        String apellido,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @FechaNacimientoValida
        LocalDate fechaNacimiento,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 30, message = "El teléfono admite hasta 30 caracteres")
        @TelefonoValido
        String telefono,

        @Size(max = 500, message = "Las observaciones admiten hasta 500 caracteres")
        String observaciones
) {

    public AlumnoRequest {
        nombre = limpiar(nombre);
        apellido = limpiar(apellido);
        telefono = limpiar(telefono);
        observaciones = observaciones == null || observaciones.isBlank() ? null : observaciones.trim();
    }

    private static String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
