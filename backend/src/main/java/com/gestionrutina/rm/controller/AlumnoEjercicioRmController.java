package com.gestionrutina.rm.controller;

import com.gestionrutina.rm.dto.AlumnoEjercicioRmRequest;
import com.gestionrutina.rm.dto.AlumnoEjercicioRmResponse;
import com.gestionrutina.rm.dto.CalculoRmRequest;
import com.gestionrutina.rm.dto.CalculoRmResponse;
import com.gestionrutina.rm.service.AlumnoEjercicioRmService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos/{alumnoId}/rm")
public class AlumnoEjercicioRmController {

    private final AlumnoEjercicioRmService rmService;

    public AlumnoEjercicioRmController(AlumnoEjercicioRmService rmService) {
        this.rmService = rmService;
    }

    @GetMapping
    public List<AlumnoEjercicioRmResponse> listar(@PathVariable Long alumnoId) {
        return rmService.listar(alumnoId);
    }

    @PutMapping
    public AlumnoEjercicioRmResponse guardar(
            @PathVariable Long alumnoId,
            @Valid @RequestBody AlumnoEjercicioRmRequest request
    ) {
        return rmService.guardar(alumnoId, request);
    }

    @DeleteMapping("/{ejercicioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long alumnoId, @PathVariable Long ejercicioId) {
        rmService.eliminar(alumnoId, ejercicioId);
    }

    @PostMapping("/calculo")
    public CalculoRmResponse calcular(@PathVariable Long alumnoId, @Valid @RequestBody CalculoRmRequest request) {
        return rmService.calcular(alumnoId, request);
    }
}
