package com.gestionrutina.ejercicio.service;

import com.gestionrutina.common.TextoBusqueda;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.dto.CategoriaEjercicioResponse;
import com.gestionrutina.ejercicio.dto.EjercicioRequest;
import com.gestionrutina.ejercicio.dto.EjercicioResponse;
import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.CategoriaEjercicioRepository;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final CategoriaEjercicioRepository categoriaRepository;

    public EjercicioService(
            EjercicioRepository ejercicioRepository,
            CategoriaEjercicioRepository categoriaRepository
    ) {
        this.ejercicioRepository = ejercicioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<EjercicioResponse> listar(String nombre, Long categoriaId, boolean incluirInactivos) {
        String texto = TextoBusqueda.normalizar(nombre);
        List<Ejercicio> ejercicios = texto.isEmpty()
                ? listarSinTexto(categoriaId, incluirInactivos)
                : ejercicioRepository.buscarPorNombre(texto, categoriaId == null ? 0L : categoriaId, categoriaId != null, incluirInactivos);
        return ejercicios.stream().map(this::aRespuesta).toList();
    }

    public EjercicioResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public EjercicioResponse crear(EjercicioRequest request) {
        CategoriaEjercicio categoria = categoriaActiva(request.categoriaId());
        asegurarNombreDisponible(request.nombre(), categoria.getId(), null);
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setNombre(request.nombre());
        ejercicio.setCategoria(categoria);
        ejercicio.setActivo(true);
        return aRespuesta(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public EjercicioResponse actualizar(Long id, EjercicioRequest request) {
        Ejercicio ejercicio = buscar(id);
        CategoriaEjercicio categoria = categoriaActiva(request.categoriaId());
        asegurarNombreDisponible(request.nombre(), categoria.getId(), id);
        ejercicio.setNombre(request.nombre());
        ejercicio.setCategoria(categoria);
        return aRespuesta(ejercicio);
    }

    @Transactional
    public void desactivar(Long id) {
        buscar(id).setActivo(false);
    }

    @Transactional
    public EjercicioResponse cambiarEstado(Long id, boolean activo) {
        Ejercicio ejercicio = buscar(id);
        if (activo && !ejercicio.getCategoria().isActivo()) {
            throw new ReglaDeNegocioException("No se puede activar el ejercicio porque su categoría está desactivada");
        }
        ejercicio.setActivo(activo);
        return aRespuesta(ejercicio);
    }

    private List<Ejercicio> listarSinTexto(Long categoriaId, boolean incluirInactivos) {
        if (categoriaId == null) {
            return incluirInactivos
                    ? ejercicioRepository.findAllByOrderByNombreAscIdAsc()
                    : ejercicioRepository.findByActivoTrueOrderByNombreAscIdAsc();
        }
        return incluirInactivos
                ? ejercicioRepository.findByCategoriaIdOrderByNombreAscIdAsc(categoriaId)
                : ejercicioRepository.findByCategoriaIdAndActivoTrueOrderByNombreAscIdAsc(categoriaId);
    }

    private Ejercicio buscar(Long id) {
        return ejercicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el ejercicio"));
    }

    private CategoriaEjercicio categoriaActiva(Long id) {
        CategoriaEjercicio categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la categoría"));
        if (!categoria.isActivo()) {
            throw new ReglaDeNegocioException("La categoría no está disponible");
        }
        return categoria;
    }

    private void asegurarNombreDisponible(String nombre, Long categoriaId, Long excluirId) {
        long coincidencias = ejercicioRepository.contarActivosConNombre(
                categoriaId,
                TextoBusqueda.normalizar(nombre),
                excluirId
        );
        if (coincidencias > 0) {
            throw new ReglaDeNegocioException("Ya existe un ejercicio activo con ese nombre en la categoría");
        }
    }

    private EjercicioResponse aRespuesta(Ejercicio ejercicio) {
        CategoriaEjercicio categoria = ejercicio.getCategoria();
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                new CategoriaEjercicioResponse(categoria.getId(), categoria.getNombre(), categoria.isActivo()),
                ejercicio.isActivo()
        );
    }
}
