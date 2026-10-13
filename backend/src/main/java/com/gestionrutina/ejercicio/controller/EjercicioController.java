package com.gestionrutina.ejercicio.controller;

import com.gestionrutina.common.dto.EstadoRequest;
import com.gestionrutina.ejercicio.dto.EjercicioRequest;
import com.gestionrutina.ejercicio.dto.EjercicioResponse;
import com.gestionrutina.ejercicio.service.EjercicioService;
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
@RequestMapping("/api/ejercicios")
@Validated
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

    @GetMapping
    public List<EjercicioResponse> listar(
            @RequestParam(required = false) @Size(max = 120, message = "La búsqueda admite hasta 120 caracteres") String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "false") boolean incluirInactivos
    ) {
        return ejercicioService.listar(nombre, categoriaId, incluirInactivos);
    }

    @GetMapping("/{id}")
    public EjercicioResponse obtener(@PathVariable Long id) {
        return ejercicioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EjercicioResponse crear(@Valid @RequestBody EjercicioRequest request) {
        return ejercicioService.crear(request);
    }

    @PutMapping("/{id}")
    public EjercicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody EjercicioRequest request) {
        return ejercicioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        ejercicioService.desactivar(id);
    }

    @PatchMapping("/{id}/estado")
    public EjercicioResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return ejercicioService.cambiarEstado(id, request.activo());
    }
}
