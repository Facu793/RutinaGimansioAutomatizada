package com.gestionrutina.rm.service;

import com.gestionrutina.alumno.entity.Alumno;
import com.gestionrutina.alumno.repository.AlumnoRepository;
import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import com.gestionrutina.rm.dto.AlumnoEjercicioRmRequest;
import com.gestionrutina.rm.dto.AlumnoEjercicioRmResponse;
import com.gestionrutina.rm.dto.CalculoRmRequest;
import com.gestionrutina.rm.dto.CalculoRmResponse;
import com.gestionrutina.rm.entity.AlumnoEjercicioRm;
import com.gestionrutina.rm.repository.AlumnoEjercicioRmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoEjercicioRmServiceTest {

    @Mock
    private AlumnoEjercicioRmRepository rmRepository;

    @Mock
    private AlumnoRepository alumnoRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    private AlumnoEjercicioRmService rmService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-08T12:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        rmService = new AlumnoEjercicioRmService(rmRepository, alumnoRepository, ejercicioRepository, clock);
    }

    @Test
    void guardarActualizaElMismoRegistro() {
        Alumno alumno = alumno();
        Ejercicio ejercicio = ejercicio(true);
        AlumnoEjercicioRm existente = new AlumnoEjercicioRm();
        existente.setId(9L);
        existente.setAlumno(alumno);
        existente.setEjercicio(ejercicio);
        existente.setPeso(new BigDecimal("90.00"));
        when(alumnoRepository.findById(3L)).thenReturn(Optional.of(alumno));
        when(ejercicioRepository.findById(5L)).thenReturn(Optional.of(ejercicio));
        when(rmRepository.findByAlumnoIdAndEjercicioId(3L, 5L)).thenReturn(Optional.of(existente));
        when(rmRepository.save(existente)).thenReturn(existente);

        AlumnoEjercicioRmResponse respuesta = rmService.guardar(3L, new AlumnoEjercicioRmRequest(5L, new BigDecimal("100")));

        assertEquals(9L, respuesta.id());
        assertEquals(new BigDecimal("100.00"), respuesta.peso());
        assertEquals(Instant.parse("2026-10-08T12:00:00Z"), respuesta.actualizadoEn());
    }

    @Test
    void noRegistraUnEjercicioInactivoPorPrimeraVez() {
        when(alumnoRepository.findById(3L)).thenReturn(Optional.of(alumno()));
        when(ejercicioRepository.findById(5L)).thenReturn(Optional.of(ejercicio(false)));
        when(rmRepository.findByAlumnoIdAndEjercicioId(3L, 5L)).thenReturn(Optional.empty());

        assertThrows(ReglaDeNegocioException.class,
                () -> rmService.guardar(3L, new AlumnoEjercicioRmRequest(5L, new BigDecimal("100"))));
        verify(rmRepository, never()).save(any());
    }

    @Test
    void calcularUsaElRmGuardado() {
        AlumnoEjercicioRm registro = new AlumnoEjercicioRm();
        registro.setPeso(new BigDecimal("100.00"));
        when(alumnoRepository.findById(3L)).thenReturn(Optional.of(alumno()));
        when(ejercicioRepository.findById(5L)).thenReturn(Optional.of(ejercicio(true)));
        when(rmRepository.findByAlumnoIdAndEjercicioId(3L, 5L)).thenReturn(Optional.of(registro));

        CalculoRmResponse calculo = rmService.calcular(3L, new CalculoRmRequest(5L, new BigDecimal("80")));

        assertEquals(new BigDecimal("80.00"), calculo.pesoCalculado());
        assertNull(calculo.advertencia());
    }

    @Test
    void calcularSinRmDejaElPesoVacio() {
        when(alumnoRepository.findById(3L)).thenReturn(Optional.of(alumno()));
        when(ejercicioRepository.findById(5L)).thenReturn(Optional.of(ejercicio(true)));
        when(rmRepository.findByAlumnoIdAndEjercicioId(3L, 5L)).thenReturn(Optional.empty());

        CalculoRmResponse calculo = rmService.calcular(3L, new CalculoRmRequest(5L, new BigDecimal("80")));

        assertNull(calculo.pesoCalculado());
        assertNull(calculo.advertencia());
        ArgumentCaptor<Long> alumnoId = ArgumentCaptor.forClass(Long.class);
        verify(alumnoRepository).findById(alumnoId.capture());
        assertEquals(3L, alumnoId.getValue());
    }

    private Alumno alumno() {
        Alumno alumno = new Alumno();
        alumno.setId(3L);
        alumno.setNombre("Juan");
        alumno.setApellido("Pérez");
        return alumno;
    }

    private Ejercicio ejercicio(boolean activo) {
        CategoriaEjercicio categoria = new CategoriaEjercicio();
        categoria.setId(1L);
        categoria.setNombre("Pecho");
        categoria.setActivo(true);
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(5L);
        ejercicio.setNombre("Press banca");
        ejercicio.setCategoria(categoria);
        ejercicio.setActivo(activo);
        return ejercicio;
    }
}
