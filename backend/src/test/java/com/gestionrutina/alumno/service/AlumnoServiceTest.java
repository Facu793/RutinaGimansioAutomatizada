package com.gestionrutina.alumno.service;

import com.gestionrutina.alumno.dto.AlumnoRequest;
import com.gestionrutina.alumno.dto.AlumnoResponse;
import com.gestionrutina.alumno.entity.Alumno;
import com.gestionrutina.alumno.repository.AlumnoRepository;
import com.gestionrutina.common.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    private AlumnoService alumnoService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-10-07T15:00:00Z"),
                ZoneId.of("America/Argentina/Buenos_Aires")
        );
        alumnoService = new AlumnoService(alumnoRepository, clock);
    }

    @Test
    void crearCalculaLaEdadYNoLaPersisteComoColumna() {
        when(alumnoRepository.save(any(Alumno.class))).thenAnswer(invocation -> {
            Alumno alumno = invocation.getArgument(0);
            alumno.setId(8L);
            return alumno;
        });

        AlumnoResponse respuesta = alumnoService.crear(new AlumnoRequest(
                " Juan ",
                "Pérez",
                LocalDate.of(1990, 1, 1),
                "11 5555 4444",
                "  "
        ));

        assertEquals(8L, respuesta.id());
        assertEquals("Juan", respuesta.nombre());
        assertEquals(36, respuesta.edad());
        assertEquals(null, respuesta.observaciones());
        assertEquals(true, respuesta.activo());
    }

    @Test
    void buscarIgnoraMayusculasYAcentos() {
        when(alumnoRepository.buscarPorNombre("perez", false)).thenReturn(List.of());

        alumnoService.listar("  PÉREZ ", false);

        verify(alumnoRepository).buscarPorNombre("perez", false);
    }

    @Test
    void desactivarMarcaAlAlumnoComoInactivo() {
        Alumno alumno = alumnoActivo();
        when(alumnoRepository.findById(8L)).thenReturn(Optional.of(alumno));

        alumnoService.desactivar(8L);

        assertFalse(alumno.isActivo());
    }

    @Test
    void obtenerInexistenteInformeQueNoExiste() {
        when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> alumnoService.obtener(99L));
    }

    @Test
    void listarSinTextoTraeSoloActivos() {
        when(alumnoRepository.findByActivoTrueOrderByApellidoAscNombreAscIdAsc()).thenReturn(List.of());

        alumnoService.listar("   ", false);

        verify(alumnoRepository).findByActivoTrueOrderByApellidoAscNombreAscIdAsc();
    }

    @Test
    void cambiarEstadoPuedeReactivar() {
        Alumno alumno = alumnoActivo();
        alumno.setActivo(false);
        when(alumnoRepository.findById(8L)).thenReturn(Optional.of(alumno));

        AlumnoResponse respuesta = alumnoService.cambiarEstado(8L, true);

        assertEquals(true, respuesta.activo());
        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        verify(alumnoRepository).findById(id.capture());
        assertEquals(8L, id.getValue());
        verify(alumnoRepository, org.mockito.Mockito.never()).save(any());
    }

    private Alumno alumnoActivo() {
        Alumno alumno = new Alumno();
        alumno.setId(8L);
        alumno.setNombre("Juan");
        alumno.setApellido("Pérez");
        alumno.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        alumno.setTelefono("1155554444");
        alumno.setActivo(true);
        return alumno;
    }
}
