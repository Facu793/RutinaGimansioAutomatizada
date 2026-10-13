package com.gestionrutina.rutina.controller;

import com.gestionrutina.common.dto.EstadoRequest;
import com.gestionrutina.rutina.dto.RutinaRequest;
import com.gestionrutina.rutina.dto.RutinaResponse;
import com.gestionrutina.rutina.dto.RutinaResumenResponse;
import com.gestionrutina.rutina.pdf.GeneradorPdfRutina;
import com.gestionrutina.rutina.pdf.PdfRutina;
import com.gestionrutina.rutina.service.RutinaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RutinaController {

    private final RutinaService rutinaService;
    private final GeneradorPdfRutina generadorPdfRutina;

    public RutinaController(RutinaService rutinaService, GeneradorPdfRutina generadorPdfRutina) {
        this.rutinaService = rutinaService;
        this.generadorPdfRutina = generadorPdfRutina;
    }

    @GetMapping("/api/alumnos/{alumnoId}/rutinas")
    public List<RutinaResumenResponse> listar(
            @PathVariable Long alumnoId,
            @RequestParam(defaultValue = "false") boolean incluirInactivas
    ) {
        return rutinaService.listarPorAlumno(alumnoId, incluirInactivas);
    }

    @GetMapping("/api/rutinas/{id}")
    public RutinaResponse obtener(@PathVariable Long id) {
        return rutinaService.obtener(id);
    }

    @GetMapping(value = "/api/rutinas/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        PdfRutina pdf = generadorPdfRutina.generar(rutinaService.obtener(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdf.nombreArchivo() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.contenido());
    }

    @PostMapping("/api/rutinas")
    @ResponseStatus(HttpStatus.CREATED)
    public RutinaResponse crear(@Valid @RequestBody RutinaRequest request) {
        return rutinaService.crear(request);
    }

    @PutMapping("/api/rutinas/{id}")
    public RutinaResponse actualizar(@PathVariable Long id, @Valid @RequestBody RutinaRequest request) {
        return rutinaService.actualizar(id, request);
    }

    @DeleteMapping("/api/rutinas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        rutinaService.desactivar(id);
    }

    @PatchMapping("/api/rutinas/{id}/estado")
    public RutinaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return rutinaService.cambiarEstado(id, request.activo());
    }
}
