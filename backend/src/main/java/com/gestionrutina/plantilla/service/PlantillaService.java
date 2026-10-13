package com.gestionrutina.plantilla.service;

import com.gestionrutina.common.TextoBusqueda;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import com.gestionrutina.plantilla.dto.EjercicioPlantillaResponse;
import com.gestionrutina.plantilla.dto.ModuloPlantillaResponse;
import com.gestionrutina.plantilla.dto.PlantillaRequest;
import com.gestionrutina.plantilla.dto.PlantillaResponse;
import com.gestionrutina.plantilla.dto.PlantillaResumenResponse;
import com.gestionrutina.plantilla.entity.EjercicioPlantilla;
import com.gestionrutina.plantilla.entity.ModuloPlantilla;
import com.gestionrutina.plantilla.entity.Plantilla;
import com.gestionrutina.plantilla.repository.PlantillaRepository;
import com.gestionrutina.rutina.dto.EjercicioRutinaRequest;
import com.gestionrutina.rutina.dto.ModuloRutinaRequest;
import com.gestionrutina.rutina.service.EjercicioRutinaReglas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlantillaService {

    private final PlantillaRepository plantillaRepository;
    private final EjercicioRepository ejercicioRepository;

    public PlantillaService(PlantillaRepository plantillaRepository, EjercicioRepository ejercicioRepository) {
        this.plantillaRepository = plantillaRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    public List<PlantillaResumenResponse> listar(String nombre, boolean incluirInactivas) {
        String texto = TextoBusqueda.normalizar(nombre);
        List<Plantilla> plantillas = texto.isEmpty()
                ? listarSinFiltro(incluirInactivas)
                : plantillaRepository.buscarPorNombre(texto, incluirInactivas);
        return plantillas.stream().map(this::aResumen).toList();
    }

    public PlantillaResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public PlantillaResponse crear(PlantillaRequest request) {
        asegurarNombreDisponible(request.nombre(), null);
        Plantilla plantilla = new Plantilla();
        plantilla.setActiva(true);
        copiarDatos(plantilla, request);
        agregarModulos(plantilla, request.modulos());
        return aRespuesta(plantillaRepository.save(plantilla));
    }

    @Transactional
    public PlantillaResponse actualizar(Long id, PlantillaRequest request) {
        Plantilla plantilla = buscar(id);
        asegurarNombreDisponible(request.nombre(), id);
        copiarDatos(plantilla, request);
        reemplazarModulos(plantilla, request.modulos());
        plantillaRepository.flush();
        return aRespuesta(plantilla);
    }

    @Transactional
    public void desactivar(Long id) {
        buscar(id).setActiva(false);
    }

    @Transactional
    public PlantillaResponse cambiarEstado(Long id, boolean activa) {
        Plantilla plantilla = buscar(id);
        if (activa) {
            asegurarNombreDisponible(plantilla.getNombre(), id);
        }
        plantilla.setActiva(activa);
        return aRespuesta(plantilla);
    }

    private List<Plantilla> listarSinFiltro(boolean incluirInactivas) {
        if (incluirInactivas) {
            return plantillaRepository.findAllByOrderByNombreAscIdAsc();
        }
        return plantillaRepository.findByActivaTrueOrderByNombreAscIdAsc();
    }

    private void reemplazarModulos(Plantilla plantilla, List<ModuloRutinaRequest> modulos) {
        for (ModuloPlantilla modulo : plantilla.getModulos()) {
            modulo.getEjercicios().size();
        }
        plantilla.getModulos().clear();
        agregarModulos(plantilla, modulos);
    }

    private void agregarModulos(Plantilla plantilla, List<ModuloRutinaRequest> modulos) {
        int ordenModulo = 1;
        for (ModuloRutinaRequest moduloRequest : modulos) {
            ModuloPlantilla modulo = new ModuloPlantilla();
            modulo.setNombre(moduloRequest.nombre());
            modulo.setOrden(ordenModulo);
            modulo.setRondas(moduloRequest.rondas());
            modulo.setDescansoEjercicioSegundos(moduloRequest.descansoEjercicioSegundos());
            modulo.setDescansoRondaSegundos(moduloRequest.descansoRondaSegundos());
            int ordenEjercicio = 1;
            for (EjercicioRutinaRequest ejercicioRequest : moduloRequest.ejercicios()) {
                EjercicioRutinaReglas.validar(moduloRequest.nombre(), ordenEjercicio, ejercicioRequest);
                modulo.agregarEjercicio(crearLinea(ejercicioRequest, ordenEjercicio));
                ordenEjercicio++;
            }
            plantilla.agregarModulo(modulo);
            ordenModulo++;
        }
    }

    private EjercicioPlantilla crearLinea(EjercicioRutinaRequest request, int orden) {
        EjercicioPlantilla linea = new EjercicioPlantilla();
        linea.setEjercicio(asegurarEjercicio(request.ejercicioId()));
        linea.setOrden(orden);
        linea.setModalidad(request.modalidad());
        linea.setRepeticiones(request.repeticiones());
        linea.setDuracionSegundos(request.duracionSegundos());
        linea.setUnidadDistancia(request.unidadDistancia());
        if (request.distancia() != null) {
            linea.setDistancia(request.distancia().setScale(2, RoundingMode.HALF_UP));
        }
        if (request.porcentajeRm() != null) {
            linea.setPorcentajeRm(request.porcentajeRm().setScale(1, RoundingMode.HALF_UP));
        }
        return linea;
    }

    private void copiarDatos(Plantilla plantilla, PlantillaRequest request) {
        plantilla.setNombre(request.nombre());
        plantilla.setObjetivo(request.objetivo());
        plantilla.setObservaciones(request.observaciones());
    }

    private void asegurarNombreDisponible(String nombre, Long excluirId) {
        long coincidencias = plantillaRepository.contarActivasConNombre(TextoBusqueda.normalizar(nombre), excluirId);
        if (coincidencias > 0) {
            throw new ReglaDeNegocioException("Ya existe una plantilla activa con ese nombre");
        }
    }

    private Ejercicio asegurarEjercicio(Long ejercicioId) {
        return ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el ejercicio"));
    }

    private Plantilla buscar(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la plantilla"));
    }

    private PlantillaResumenResponse aResumen(Plantilla plantilla) {
        return new PlantillaResumenResponse(
                plantilla.getId(),
                plantilla.getNombre(),
                plantilla.getObjetivo(),
                plantilla.isActiva()
        );
    }

    private PlantillaResponse aRespuesta(Plantilla plantilla) {
        List<ModuloPlantillaResponse> modulos = plantilla.getModulos().stream()
                .sorted(Comparator.comparingInt(ModuloPlantilla::getOrden))
                .map(this::aModulo)
                .toList();
        return new PlantillaResponse(
                plantilla.getId(),
                plantilla.getNombre(),
                plantilla.getObjetivo(),
                plantilla.getObservaciones(),
                plantilla.isActiva(),
                modulos,
                advertencias(plantilla)
        );
    }

    private ModuloPlantillaResponse aModulo(ModuloPlantilla modulo) {
        List<EjercicioPlantillaResponse> ejercicios = modulo.getEjercicios().stream()
                .sorted(Comparator.comparingInt(EjercicioPlantilla::getOrden))
                .map(this::aEjercicio)
                .toList();
        return new ModuloPlantillaResponse(
                modulo.getId(),
                modulo.getNombre(),
                modulo.getOrden(),
                modulo.getRondas(),
                modulo.getDescansoEjercicioSegundos(),
                modulo.getDescansoRondaSegundos(),
                ejercicios
        );
    }

    private EjercicioPlantillaResponse aEjercicio(EjercicioPlantilla linea) {
        Ejercicio ejercicio = linea.getEjercicio();
        return new EjercicioPlantillaResponse(
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
                linea.getPorcentajeRm()
        );
    }

    private List<String> advertencias(Plantilla plantilla) {
        List<String> advertencias = new ArrayList<>();
        for (ModuloPlantilla modulo : plantilla.getModulos()) {
            for (EjercicioPlantilla linea : modulo.getEjercicios()) {
                if (!linea.getEjercicio().isActivo()) {
                    advertencias.add(modulo.getNombre() + " - " + linea.getEjercicio().getNombre()
                            + ": el ejercicio está desactivado en el catálogo");
                }
            }
        }
        return advertencias;
    }
}
