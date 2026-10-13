package com.gestionrutina.rutina.service;

import com.gestionrutina.alumno.entity.Alumno;
import com.gestionrutina.alumno.repository.AlumnoRepository;
import com.gestionrutina.common.exception.PeticionInvalidaException;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import com.gestionrutina.rm.CalculadoraRm;
import com.gestionrutina.rm.entity.AlumnoEjercicioRm;
import com.gestionrutina.rm.repository.AlumnoEjercicioRmRepository;
import com.gestionrutina.rutina.dto.EjercicioRutinaRequest;
import com.gestionrutina.rutina.dto.EjercicioRutinaResponse;
import com.gestionrutina.rutina.dto.ModuloRutinaRequest;
import com.gestionrutina.rutina.dto.ModuloRutinaResponse;
import com.gestionrutina.rutina.dto.RutinaRequest;
import com.gestionrutina.rutina.dto.RutinaResponse;
import com.gestionrutina.rutina.dto.RutinaResumenResponse;
import com.gestionrutina.rutina.entity.EjercicioRutina;
import com.gestionrutina.rutina.entity.ModuloRutina;
import com.gestionrutina.rutina.entity.Rutina;
import com.gestionrutina.rutina.repository.RutinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final AlumnoRepository alumnoRepository;
    private final EjercicioRepository ejercicioRepository;
    private final AlumnoEjercicioRmRepository rmRepository;
    private final Clock clock;

    public RutinaService(
            RutinaRepository rutinaRepository,
            AlumnoRepository alumnoRepository,
            EjercicioRepository ejercicioRepository,
            AlumnoEjercicioRmRepository rmRepository,
            Clock clock
    ) {
        this.rutinaRepository = rutinaRepository;
        this.alumnoRepository = alumnoRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.rmRepository = rmRepository;
        this.clock = clock;
    }

    public List<RutinaResumenResponse> listarPorAlumno(Long alumnoId, boolean incluirInactivas) {
        asegurarAlumno(alumnoId);
        List<Rutina> rutinas = incluirInactivas
                ? rutinaRepository.findByAlumnoIdOrderByFechaInicioDescIdDesc(alumnoId)
                : rutinaRepository.findByAlumnoIdAndActivaTrueOrderByFechaInicioDescIdDesc(alumnoId);
        return rutinas.stream().map(this::aResumen).toList();
    }

    public RutinaResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public RutinaResponse crear(RutinaRequest request) {
        Alumno alumno = asegurarAlumno(request.alumnoId());
        validarFechas(alumno, request);
        Rutina rutina = new Rutina();
        rutina.setAlumno(alumno);
        rutina.setFechaCreacion(LocalDate.now(clock));
        rutina.setActiva(true);
        copiarDatos(rutina, request);
        agregarModulos(rutina, request.modulos(), pesosRm(alumno.getId()));
        return aRespuesta(rutinaRepository.save(rutina));
    }

    @Transactional
    public RutinaResponse actualizar(Long id, RutinaRequest request) {
        Rutina rutina = buscar(id);
        if (!rutina.getAlumno().getId().equals(request.alumnoId())) {
            throw new PeticionInvalidaException("No se puede cambiar el alumno de una rutina");
        }
        validarFechas(rutina.getAlumno(), request);
        copiarDatos(rutina, request);
        reemplazarModulos(rutina, request.modulos(), pesosRm(rutina.getAlumno().getId()));
        rutinaRepository.flush();
        return aRespuesta(rutina);
    }

    @Transactional
    public void desactivar(Long id) {
        buscar(id).setActiva(false);
    }

    @Transactional
    public RutinaResponse cambiarEstado(Long id, boolean activa) {
        Rutina rutina = buscar(id);
        rutina.setActiva(activa);
        return aRespuesta(rutina);
    }

    private void reemplazarModulos(Rutina rutina, List<ModuloRutinaRequest> modulos, Map<Long, BigDecimal> pesos) {
        for (ModuloRutina modulo : rutina.getModulos()) {
            modulo.getEjercicios().size();
        }
        rutina.getModulos().clear();
        agregarModulos(rutina, modulos, pesos);
    }

    private void agregarModulos(Rutina rutina, List<ModuloRutinaRequest> modulos, Map<Long, BigDecimal> pesos) {
        int ordenModulo = 1;
        for (ModuloRutinaRequest moduloRequest : modulos) {
            ModuloRutina modulo = new ModuloRutina();
            modulo.setNombre(moduloRequest.nombre());
            modulo.setOrden(ordenModulo);
            modulo.setRondas(moduloRequest.rondas());
            modulo.setDescansoEjercicioSegundos(moduloRequest.descansoEjercicioSegundos());
            modulo.setDescansoRondaSegundos(moduloRequest.descansoRondaSegundos());
            int ordenEjercicio = 1;
            for (EjercicioRutinaRequest ejercicioRequest : moduloRequest.ejercicios()) {
                EjercicioRutinaReglas.validar(moduloRequest.nombre(), ordenEjercicio, ejercicioRequest);
                modulo.agregarEjercicio(crearLinea(ejercicioRequest, ordenEjercicio, pesos));
                ordenEjercicio++;
            }
            rutina.agregarModulo(modulo);
            ordenModulo++;
        }
    }

    private EjercicioRutina crearLinea(EjercicioRutinaRequest request, int orden, Map<Long, BigDecimal> pesos) {
        Ejercicio ejercicio = asegurarEjercicio(request.ejercicioId());
        EjercicioRutina linea = new EjercicioRutina();
        linea.setEjercicio(ejercicio);
        linea.setOrden(orden);
        linea.setModalidad(request.modalidad());
        linea.setRepeticiones(request.repeticiones());
        linea.setDuracionSegundos(request.duracionSegundos());
        linea.setUnidadDistancia(request.unidadDistancia());
        if (request.distancia() != null) {
            linea.setDistancia(request.distancia().setScale(2, RoundingMode.HALF_UP));
        }
        if (request.porcentajeRm() != null) {
            BigDecimal porcentaje = request.porcentajeRm().setScale(1, RoundingMode.HALF_UP);
            linea.setPorcentajeRm(porcentaje);
            BigDecimal rm = pesos.get(ejercicio.getId());
            if (rm != null) {
                linea.setPesoCalculado(CalculadoraRm.pesoRecomendado(rm, porcentaje));
            }
        }
        return linea;
    }

    private Map<Long, BigDecimal> pesosRm(Long alumnoId) {
        return rmRepository.listarPorAlumno(alumnoId).stream()
                .collect(Collectors.toMap(
                        registro -> registro.getEjercicio().getId(),
                        AlumnoEjercicioRm::getPeso,
                        (actual, ignorado) -> actual
                ));
    }

    private void copiarDatos(Rutina rutina, RutinaRequest request) {
        rutina.setNombre(request.nombre());
        rutina.setObjetivo(request.objetivo());
        rutina.setFechaInicio(request.fechaInicio());
        rutina.setFechaFin(request.fechaFin());
        rutina.setObservaciones(request.observaciones());
    }

    private void validarFechas(Alumno alumno, RutinaRequest request) {
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new PeticionInvalidaException("La fecha de finalización no puede ser anterior a la fecha de inicio");
        }
        if (request.fechaInicio().isBefore(alumno.getFechaNacimiento())) {
            throw new PeticionInvalidaException("La fecha de inicio no puede ser anterior al nacimiento del alumno");
        }
    }

    private Alumno asegurarAlumno(Long alumnoId) {
        return alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el alumno"));
    }

    private Ejercicio asegurarEjercicio(Long ejercicioId) {
        return ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el ejercicio"));
    }

    private Rutina buscar(Long id) {
        return rutinaRepository.buscarConAlumno(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la rutina"));
    }

    private RutinaResumenResponse aResumen(Rutina rutina) {
        return new RutinaResumenResponse(
                rutina.getId(),
                rutina.getAlumno().getId(),
                rutina.getNombre(),
                rutina.getObjetivo(),
                rutina.getFechaCreacion(),
                rutina.getFechaInicio(),
                rutina.getFechaFin(),
                rutina.isActiva()
        );
    }

    private RutinaResponse aRespuesta(Rutina rutina) {
        Alumno alumno = rutina.getAlumno();
        List<ModuloRutinaResponse> modulos = rutina.getModulos().stream()
                .sorted(Comparator.comparingInt(ModuloRutina::getOrden))
                .map(this::aModulo)
                .toList();
        return new RutinaResponse(
                rutina.getId(),
                alumno.getId(),
                alumno.getNombre(),
                alumno.getApellido(),
                Period.between(alumno.getFechaNacimiento(), rutina.getFechaInicio()).getYears(),
                rutina.getNombre(),
                rutina.getObjetivo(),
                rutina.getFechaCreacion(),
                rutina.getFechaInicio(),
                rutina.getFechaFin(),
                rutina.getObservaciones(),
                rutina.isActiva(),
                modulos,
                advertencias(rutina)
        );
    }

    private ModuloRutinaResponse aModulo(ModuloRutina modulo) {
        List<EjercicioRutinaResponse> ejercicios = modulo.getEjercicios().stream()
                .sorted(Comparator.comparingInt(EjercicioRutina::getOrden))
                .map(this::aEjercicio)
                .toList();
        return new ModuloRutinaResponse(
                modulo.getId(),
                modulo.getNombre(),
                modulo.getOrden(),
                modulo.getRondas(),
                modulo.getDescansoEjercicioSegundos(),
                modulo.getDescansoRondaSegundos(),
                ejercicios
        );
    }

    private EjercicioRutinaResponse aEjercicio(EjercicioRutina linea) {
        Ejercicio ejercicio = linea.getEjercicio();
        return new EjercicioRutinaResponse(
                linea.getId(),
                linea.getOrden(),
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getCategoria().getNombre(),
                ejercicio.isActivo(),
                linea.getModalidad(),
                linea.getRepeticiones(),
                linea.getDuracionSegundos(),
                linea.getDistancia(),
                linea.getUnidadDistancia(),
                linea.getPorcentajeRm(),
                linea.getPesoCalculado()
        );
    }

    private List<String> advertencias(Rutina rutina) {
        List<String> advertencias = new ArrayList<>();
        for (ModuloRutina modulo : rutina.getModulos()) {
            for (EjercicioRutina linea : modulo.getEjercicios()) {
                if (linea.getPorcentajeRm() != null && linea.getPesoCalculado() == null) {
                    advertencias.add(modulo.getNombre() + " - " + linea.getEjercicio().getNombre()
                            + ": no hay un 1RM registrado, el peso no se pudo calcular");
                }
                if (!linea.getEjercicio().isActivo()) {
                    advertencias.add(modulo.getNombre() + " - " + linea.getEjercicio().getNombre()
                            + ": el ejercicio está desactivado en el catálogo");
                }
            }
        }
        return advertencias;
    }
}
