package com.gestionrutina.ejercicio.service;

import com.gestionrutina.common.TextoBusqueda;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.dto.CategoriaEjercicioRequest;
import com.gestionrutina.ejercicio.dto.CategoriaEjercicioResponse;
import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import com.gestionrutina.ejercicio.repository.CategoriaEjercicioRepository;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaEjercicioService {

    private final CategoriaEjercicioRepository categoriaRepository;
    private final EjercicioRepository ejercicioRepository;

    public CategoriaEjercicioService(
            CategoriaEjercicioRepository categoriaRepository,
            EjercicioRepository ejercicioRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    public List<CategoriaEjercicioResponse> listar(String nombre, boolean incluirInactivos) {
        String texto = TextoBusqueda.normalizar(nombre);
        List<CategoriaEjercicio> categorias = texto.isEmpty()
                ? listarSinFiltro(incluirInactivos)
                : categoriaRepository.buscarPorNombre(texto, incluirInactivos);
        return categorias.stream().map(this::aRespuesta).toList();
    }

    public CategoriaEjercicioResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public CategoriaEjercicioResponse crear(CategoriaEjercicioRequest request) {
        asegurarNombreDisponible(request.nombre(), null);
        CategoriaEjercicio categoria = new CategoriaEjercicio();
        categoria.setNombre(request.nombre());
        categoria.setActivo(true);
        return aRespuesta(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaEjercicioResponse actualizar(Long id, CategoriaEjercicioRequest request) {
        CategoriaEjercicio categoria = buscar(id);
        asegurarNombreDisponible(request.nombre(), id);
        categoria.setNombre(request.nombre());
        return aRespuesta(categoria);
    }

    @Transactional
    public void desactivar(Long id) {
        asegurarSinEjerciciosActivos(id);
        buscar(id).setActivo(false);
    }

    @Transactional
    public CategoriaEjercicioResponse cambiarEstado(Long id, boolean activo) {
        if (!activo) {
            asegurarSinEjerciciosActivos(id);
        }
        CategoriaEjercicio categoria = buscar(id);
        categoria.setActivo(activo);
        return aRespuesta(categoria);
    }

    private List<CategoriaEjercicio> listarSinFiltro(boolean incluirInactivos) {
        if (incluirInactivos) {
            return categoriaRepository.findAllByOrderByNombreAscIdAsc();
        }
        return categoriaRepository.findByActivoTrueOrderByNombreAscIdAsc();
    }

    private CategoriaEjercicio buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la categoría"));
    }

    private void asegurarNombreDisponible(String nombre, Long excluirId) {
        long coincidencias = categoriaRepository.contarActivasConNombre(TextoBusqueda.normalizar(nombre), excluirId);
        if (coincidencias > 0) {
            throw new ReglaDeNegocioException("Ya existe una categoría activa con ese nombre");
        }
    }

    private void asegurarSinEjerciciosActivos(Long categoriaId) {
        if (ejercicioRepository.countByCategoriaIdAndActivoTrue(categoriaId) > 0) {
            throw new ReglaDeNegocioException("No se puede desactivar la categoría porque tiene ejercicios activos");
        }
    }

    private CategoriaEjercicioResponse aRespuesta(CategoriaEjercicio categoria) {
        return new CategoriaEjercicioResponse(categoria.getId(), categoria.getNombre(), categoria.isActivo());
    }
}
