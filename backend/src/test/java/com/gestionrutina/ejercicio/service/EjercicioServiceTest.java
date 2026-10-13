package com.gestionrutina.ejercicio.service;

import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.dto.EjercicioRequest;
import com.gestionrutina.ejercicio.dto.EjercicioResponse;
import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.CategoriaEjercicioRepository;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjercicioServiceTest {

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private CategoriaEjercicioRepository categoriaRepository;

    private EjercicioService ejercicioService;

    @BeforeEach
    void setUp() {
        ejercicioService = new EjercicioService(ejercicioRepository, categoriaRepository);
    }

    @Test
    void buscarNormalizaElTextoParaElAutocompletado() {
        when(ejercicioRepository.buscarPorNombre("pres", 0L, false, false)).thenReturn(List.of());

        ejercicioService.listar("  Prés ", null, false);

        verify(ejercicioRepository).buscarPorNombre("pres", 0L, false, false);
    }

    @Test
    void crearRechazaUnNombreRepetidoEnLaMismaCategoria() {
        CategoriaEjercicio pecho = categoria(1L, "Pecho", true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(pecho));
        when(ejercicioRepository.contarActivosConNombre(1L, "press banca", null)).thenReturn(1L);

        assertThrows(ReglaDeNegocioException.class, () -> ejercicioService.crear(new EjercicioRequest("Press banca", 1L)));
        verify(ejercicioRepository, never()).save(any());
    }

    @Test
    void crearGuardaElEjercicioCuandoElNombreEstaLibre() {
        CategoriaEjercicio pecho = categoria(1L, "Pecho", true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(pecho));
        when(ejercicioRepository.contarActivosConNombre(1L, "press banca", null)).thenReturn(0L);
        when(ejercicioRepository.save(any(Ejercicio.class))).thenAnswer(invocation -> {
            Ejercicio ejercicio = invocation.getArgument(0);
            ejercicio.setId(4L);
            return ejercicio;
        });

        EjercicioResponse respuesta = ejercicioService.crear(new EjercicioRequest("  Press   banca ", 1L));

        assertEquals("Press banca", respuesta.nombre());
        assertEquals("Pecho", respuesta.categoria().nombre());
        assertEquals(true, respuesta.activo());
    }

    @Test
    void noPermiteActivarUnEjercicioDeUnaCategoriaInactiva() {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(4L);
        ejercicio.setNombre("Press banca");
        ejercicio.setCategoria(categoria(1L, "Pecho", false));
        ejercicio.setActivo(false);
        when(ejercicioRepository.findById(4L)).thenReturn(Optional.of(ejercicio));

        assertThrows(ReglaDeNegocioException.class, () -> ejercicioService.cambiarEstado(4L, true));
    }

    @Test
    void desactivarCategoriaConEjerciciosActivosFalla() {
        CategoriaEjercicioService categoriaService = new CategoriaEjercicioService(categoriaRepository, ejercicioRepository);
        when(ejercicioRepository.countByCategoriaIdAndActivoTrue(1L)).thenReturn(2L);

        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.desactivar(1L));
        verify(categoriaRepository, never()).findById(eq(1L));
    }

    private CategoriaEjercicio categoria(Long id, String nombre, boolean activo) {
        CategoriaEjercicio categoria = new CategoriaEjercicio();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setActivo(activo);
        return categoria;
    }
}
