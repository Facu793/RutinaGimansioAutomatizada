package com.gestionrutina.plantilla.controller;

import com.gestionrutina.common.dto.EstadoRequest;
import com.gestionrutina.plantilla.dto.PlantillaRequest;
import com.gestionrutina.plantilla.dto.PlantillaResponse;
import com.gestionrutina.plantilla.dto.PlantillaResumenResponse;
import com.gestionrutina.plantilla.service.PlantillaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/plantillas")
public class PlantillaController {

    private final PlantillaService plantillaService;

    public PlantillaController(PlantillaService plantillaService) {
        this.plantillaService = plantillaService;
    }

    @GetMapping
    public List<PlantillaResumenResponse> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "false") boolean incluirInactivas
    ) {
        return plantillaService.listar(nombre, incluirInactivas);
    }

    @GetMapping("/{id}")
    public PlantillaResponse obtener(@PathVariable Long id) {
        return plantillaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlantillaResponse crear(@Valid @RequestBody PlantillaRequest request) {
        return plantillaService.crear(request);
    }

    @PutMapping("/{id}")
    public PlantillaResponse actualizar(@PathVariable Long id, @Valid @RequestBody PlantillaRequest request) {
        return plantillaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        plantillaService.desactivar(id);
    }

    @PatchMapping("/{id}/estado")
    public PlantillaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return plantillaService.cambiarEstado(id, request.activo());
    }
}
