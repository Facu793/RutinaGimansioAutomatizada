package com.gestionrutina.alumno.dto;

import java.time.LocalDate;

public record AlumnoResponse(
        Long id,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        int edad,
        String telefono,
        String observaciones,
        boolean activo
) {
}
