package com.gestionrutina.alumno.controller;

import com.gestionrutina.alumno.dto.AlumnoEstadoRequest;
import com.gestionrutina.alumno.dto.AlumnoRequest;
import com.gestionrutina.alumno.dto.AlumnoResponse;
import com.gestionrutina.alumno.service.AlumnoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@Validated
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public List<AlumnoResponse> listar(
            @RequestParam(required = false) @Size(max = 120, message = "La búsqueda admite hasta 120 caracteres") String nombre,
            @RequestParam(defaultValue = "false") boolean incluirInactivos
    ) {
        return alumnoService.listar(nombre, incluirInactivos);
    }

    @GetMapping("/{id}")
    public AlumnoResponse obtener(@PathVariable Long id) {
        return alumnoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlumnoResponse crear(@Valid @RequestBody AlumnoRequest request) {
        return alumnoService.crear(request);
    }

    @PutMapping("/{id}")
    public AlumnoResponse actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoRequest request) {
        return alumnoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        alumnoService.desactivar(id);
    }

    @PatchMapping("/{id}/estado")
    public AlumnoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody AlumnoEstadoRequest request) {
        return alumnoService.cambiarEstado(id, request.activo());
    }
}
