package com.gestionrutina.alumno.service;

import com.gestionrutina.alumno.dto.AlumnoRequest;
import com.gestionrutina.alumno.dto.AlumnoResponse;
import com.gestionrutina.alumno.entity.Alumno;
import com.gestionrutina.alumno.repository.AlumnoRepository;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final Clock clock;

    public AlumnoService(AlumnoRepository alumnoRepository, Clock clock) {
        this.alumnoRepository = alumnoRepository;
        this.clock = clock;
    }

    public List<AlumnoResponse> listar(String nombre, boolean incluirInactivos) {
        String texto = normalizarBusqueda(nombre);
        List<Alumno> alumnos = texto.isEmpty()
                ? listarSinFiltro(incluirInactivos)
                : alumnoRepository.buscarPorNombre(texto, incluirInactivos);
        return alumnos.stream().map(this::aRespuesta).toList();
    }

    public AlumnoResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public AlumnoResponse crear(AlumnoRequest request) {
        Alumno alumno = new Alumno();
        copiarDatos(alumno, request);
        alumno.setActivo(true);
        return aRespuesta(alumnoRepository.save(alumno));
    }

    @Transactional
    public AlumnoResponse actualizar(Long id, AlumnoRequest request) {
        Alumno alumno = buscar(id);
        copiarDatos(alumno, request);
        return aRespuesta(alumno);
    }

    @Transactional
    public void desactivar(Long id) {
        buscar(id).setActivo(false);
    }

    @Transactional
    public AlumnoResponse cambiarEstado(Long id, boolean activo) {
        Alumno alumno = buscar(id);
        alumno.setActivo(activo);
        return aRespuesta(alumno);
    }

    private List<Alumno> listarSinFiltro(boolean incluirInactivos) {
        if (incluirInactivos) {
            return alumnoRepository.findAllByOrderByApellidoAscNombreAscIdAsc();
        }
        return alumnoRepository.findByActivoTrueOrderByApellidoAscNombreAscIdAsc();
    }

    private Alumno buscar(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el alumno"));
    }

    private void copiarDatos(Alumno alumno, AlumnoRequest request) {
        alumno.setNombre(request.nombre());
        alumno.setApellido(request.apellido());
        alumno.setFechaNacimiento(request.fechaNacimiento());
        alumno.setTelefono(request.telefono());
        alumno.setObservaciones(request.observaciones());
    }

    private AlumnoResponse aRespuesta(Alumno alumno) {
        return new AlumnoResponse(
                alumno.getId(),
                alumno.getNombre(),
                alumno.getApellido(),
                alumno.getFechaNacimiento(),
                Period.between(alumno.getFechaNacimiento(), LocalDate.now(clock)).getYears(),
                alumno.getTelefono(),
                alumno.getObservaciones(),
                alumno.isActivo()
        );
    }

    private String normalizarBusqueda(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinAcentos.toLowerCase(Locale.ROOT)
                .replace("%", "")
                .replace("_", "");
    }
}
