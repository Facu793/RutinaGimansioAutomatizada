package com.gestionrutina.ejercicio.controller;

import com.gestionrutina.common.dto.EstadoRequest;
import com.gestionrutina.ejercicio.dto.CategoriaEjercicioRequest;
import com.gestionrutina.ejercicio.dto.CategoriaEjercicioResponse;
import com.gestionrutina.ejercicio.service.CategoriaEjercicioService;
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
@RequestMapping("/api/categorias")
@Validated
public class CategoriaEjercicioController {

    private final CategoriaEjercicioService categoriaService;

    public CategoriaEjercicioController(CategoriaEjercicioService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaEjercicioResponse> listar(
            @RequestParam(required = false) @Size(max = 60, message = "La búsqueda admite hasta 60 caracteres") String nombre,
            @RequestParam(defaultValue = "false") boolean incluirInactivos
    ) {
        return categoriaService.listar(nombre, incluirInactivos);
    }

    @GetMapping("/{id}")
    public CategoriaEjercicioResponse obtener(@PathVariable Long id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaEjercicioResponse crear(@Valid @RequestBody CategoriaEjercicioRequest request) {
        return categoriaService.crear(request);
    }

    @PutMapping("/{id}")
    public CategoriaEjercicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaEjercicioRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        categoriaService.desactivar(id);
    }

    @PatchMapping("/{id}/estado")
    public CategoriaEjercicioResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return categoriaService.cambiarEstado(id, request.activo());
    }
}
