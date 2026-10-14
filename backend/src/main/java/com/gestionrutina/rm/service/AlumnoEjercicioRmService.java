package com.gestionrutina.rm.service;

import com.gestionrutina.alumno.entity.Alumno;
import com.gestionrutina.alumno.repository.AlumnoRepository;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import com.gestionrutina.rm.CalculadoraRm;
import com.gestionrutina.rm.dto.AlumnoEjercicioRmRequest;
import com.gestionrutina.rm.dto.AlumnoEjercicioRmResponse;
import com.gestionrutina.rm.dto.CalculoRmRequest;
import com.gestionrutina.rm.dto.CalculoRmResponse;
import com.gestionrutina.rm.entity.AlumnoEjercicioRm;
import com.gestionrutina.rm.repository.AlumnoEjercicioRmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AlumnoEjercicioRmService {

    static final String SIN_RM = "No hay un 1RM registrado para este alumno y ejercicio";

    private final AlumnoEjercicioRmRepository rmRepository;
    private final AlumnoRepository alumnoRepository;
    private final EjercicioRepository ejercicioRepository;
    private final Clock clock;

    public AlumnoEjercicioRmService(
            AlumnoEjercicioRmRepository rmRepository,
            AlumnoRepository alumnoRepository,
            EjercicioRepository ejercicioRepository,
            Clock clock
    ) {
        this.rmRepository = rmRepository;
        this.alumnoRepository = alumnoRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.clock = clock;
    }

    public List<AlumnoEjercicioRmResponse> listar(Long alumnoId) {
        asegurarAlumno(alumnoId);
        return rmRepository.listarPorAlumno(alumnoId).stream().map(this::aRespuesta).toList();
    }

    @Transactional
    public AlumnoEjercicioRmResponse guardar(Long alumnoId, AlumnoEjercicioRmRequest request) {
        Alumno alumno = asegurarAlumno(alumnoId);
        Ejercicio ejercicio = asegurarEjercicio(request.ejercicioId());
        Optional<AlumnoEjercicioRm> existente = rmRepository.findByAlumnoIdAndEjercicioId(alumnoId, ejercicio.getId());
        if (existente.isEmpty() && !ejercicio.isActivo()) {
            throw new ReglaDeNegocioException("El ejercicio no está disponible");
        }

        AlumnoEjercicioRm registro = existente.orElseGet(AlumnoEjercicioRm::new);
        registro.setAlumno(alumno);
        registro.setEjercicio(ejercicio);
        registro.setPeso(request.peso().setScale(2, RoundingMode.HALF_UP));
        registro.setActualizadoEn(Instant.now(clock));
        return aRespuesta(rmRepository.save(registro));
    }

    @Transactional
    public void eliminar(Long alumnoId, Long ejercicioId) {
        asegurarAlumno(alumnoId);
        AlumnoEjercicioRm registro = rmRepository.findByAlumnoIdAndEjercicioId(alumnoId, ejercicioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(SIN_RM));
        rmRepository.delete(registro);
    }

    public CalculoRmResponse calcular(Long alumnoId, CalculoRmRequest request) {
        asegurarAlumno(alumnoId);
        Ejercicio ejercicio = asegurarEjercicio(request.ejercicioId());
        Optional<AlumnoEjercicioRm> registro = rmRepository.findByAlumnoIdAndEjercicioId(alumnoId, ejercicio.getId());
        if (registro.isEmpty()) {
            return new CalculoRmResponse(
                    ejercicio.getId(),
                    ejercicio.getNombre(),
                    request.porcentaje(),
                    null,
                    null,
                    null
            );
        }

        BigDecimal rm = registro.get().getPeso();
        return new CalculoRmResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                request.porcentaje(),
                rm,
                CalculadoraRm.pesoRecomendado(rm, request.porcentaje()),
                null
        );
    }

    private Alumno asegurarAlumno(Long alumnoId) {
        return alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el alumno"));
    }

    private Ejercicio asegurarEjercicio(Long ejercicioId) {
        return ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el ejercicio"));
    }

    private AlumnoEjercicioRmResponse aRespuesta(AlumnoEjercicioRm registro) {
        Ejercicio ejercicio = registro.getEjercicio();
        return new AlumnoEjercicioRmResponse(
                registro.getId(),
                registro.getAlumno().getId(),
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getCategoria().getNombre(),
                registro.getPeso(),
                registro.getActualizadoEn()
        );
    }
}
