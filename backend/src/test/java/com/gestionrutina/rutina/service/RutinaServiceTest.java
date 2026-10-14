package com.gestionrutina.rutina.service;

import com.gestionrutina.alumno.entity.Alumno;
import com.gestionrutina.alumno.repository.AlumnoRepository;
import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import com.gestionrutina.rm.entity.AlumnoEjercicioRm;
import com.gestionrutina.rm.repository.AlumnoEjercicioRmRepository;
import com.gestionrutina.rutina.dto.EjercicioRutinaRequest;
import com.gestionrutina.rutina.dto.ModuloRutinaRequest;
import com.gestionrutina.rutina.dto.RutinaRequest;
import com.gestionrutina.rutina.dto.RutinaResponse;
import com.gestionrutina.rutina.entity.EjercicioRutina;
import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import com.gestionrutina.rutina.entity.ModuloRutina;
import com.gestionrutina.rutina.entity.Rutina;
import com.gestionrutina.rutina.repository.RutinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RutinaServiceTest {

    @Mock
    private RutinaRepository rutinaRepository;

    @Mock
    private AlumnoRepository alumnoRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private AlumnoEjercicioRmRepository rmRepository;

    private RutinaService rutinaService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-13T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        rutinaService = new RutinaService(rutinaRepository, alumnoRepository, ejercicioRepository, rmRepository, clock);
    }

    @Test
    void guardaElPorcentajeYCalculaElPesoConElRm() {
        prepararAlumnoYEjercicio(new BigDecimal("100.00"));
        when(rutinaRepository.save(any(Rutina.class))).thenAnswer(invocation -> {
            Rutina rutina = invocation.getArgument(0);
            rutina.setId(15L);
            return rutina;
        });

        RutinaResponse respuesta = rutinaService.crear(solicitud(new BigDecimal("80"), 5));

        assertEquals(new BigDecimal("80.0"), respuesta.modulos().getFirst().ejercicios().getFirst().porcentajeRm());
        assertEquals(new BigDecimal("80.00"), respuesta.modulos().getFirst().ejercicios().getFirst().pesoCalculado());
        assertEquals(36, respuesta.edad());
        assertTrue(respuesta.advertencias().isEmpty());
    }

    @Test
    void guardaElPorcentajeAunqueNoHayaRm() {
        prepararAlumnoYEjercicio(null);
        when(rutinaRepository.save(any(Rutina.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RutinaResponse respuesta = rutinaService.crear(solicitud(new BigDecimal("80"), 5));

        assertEquals(new BigDecimal("80.0"), respuesta.modulos().getFirst().ejercicios().getFirst().porcentajeRm());
        assertNull(respuesta.modulos().getFirst().ejercicios().getFirst().pesoCalculado());
        assertTrue(respuesta.advertencias().isEmpty());
    }

    @Test
    void editarLaCopiaNoModificaLaRutinaOriginal() {
        Rutina original = rutinaConRondas(1L, 5);
        Rutina copia = rutinaConRondas(2L, 5);
        Ejercicio ejercicio = copia.getModulos().getFirst().getEjercicios().getFirst().getEjercicio();
        when(ejercicioRepository.findById(4L)).thenReturn(Optional.of(ejercicio));
        AlumnoEjercicioRm registro = new AlumnoEjercicioRm();
        registro.setEjercicio(ejercicio);
        registro.setPeso(new BigDecimal("100.00"));
        when(rmRepository.listarPorAlumno(3L)).thenReturn(List.of(registro));
        when(rutinaRepository.buscarConAlumno(2L)).thenReturn(Optional.of(copia));

        rutinaService.actualizar(2L, solicitud(new BigDecimal("70"), 8));

        assertEquals(5, original.getModulos().getFirst().getRondas());
        assertEquals(8, copia.getModulos().getFirst().getRondas());
        assertEquals(new BigDecimal("80.00"), original.getModulos().getFirst().getEjercicios().getFirst().getPesoCalculado());
        assertEquals(new BigDecimal("70.00"), copia.getModulos().getFirst().getEjercicios().getFirst().getPesoCalculado());
    }

    private void prepararAlumnoYEjercicio(BigDecimal rm) {
        Alumno alumno = new Alumno();
        alumno.setId(3L);
        alumno.setNombre("Juan");
        alumno.setApellido("Pérez");
        alumno.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        when(alumnoRepository.findById(3L)).thenReturn(Optional.of(alumno));

        CategoriaEjercicio categoria = new CategoriaEjercicio();
        categoria.setNombre("Pecho");
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(4L);
        ejercicio.setNombre("Press banca");
        ejercicio.setCategoria(categoria);
        ejercicio.setActivo(true);
        when(ejercicioRepository.findById(4L)).thenReturn(Optional.of(ejercicio));

        if (rm == null) {
            when(rmRepository.listarPorAlumno(3L)).thenReturn(List.of());
            return;
        }
        AlumnoEjercicioRm registro = new AlumnoEjercicioRm();
        registro.setEjercicio(ejercicio);
        registro.setPeso(rm);
        when(rmRepository.listarPorAlumno(3L)).thenReturn(List.of(registro));
    }

    private Rutina rutinaConRondas(Long id, int rondas) {
        Alumno alumno = new Alumno();
        alumno.setId(3L);
        alumno.setNombre("Juan");
        alumno.setApellido("Pérez");
        alumno.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        Rutina rutina = new Rutina();
        rutina.setId(id);
        rutina.setAlumno(alumno);
        rutina.setNombre("Fuerza octubre");
        rutina.setObjetivo("Fuerza");
        rutina.setFechaCreacion(LocalDate.of(2026, 10, 13));
        rutina.setFechaInicio(LocalDate.of(2026, 10, 1));
        rutina.setFechaFin(LocalDate.of(2026, 10, 31));
        rutina.setActiva(true);
        ModuloRutina modulo = new ModuloRutina();
        modulo.setNombre("Módulo 1");
        modulo.setOrden(1);
        modulo.setRondas(rondas);
        modulo.setDescansoEjercicioSegundos(0);
        modulo.setDescansoRondaSegundos(90);
        CategoriaEjercicio categoria = new CategoriaEjercicio();
        categoria.setNombre("Pecho");
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(4L);
        ejercicio.setNombre("Press banca");
        ejercicio.setCategoria(categoria);
        ejercicio.setActivo(true);
        EjercicioRutina linea = new EjercicioRutina();
        linea.setEjercicio(ejercicio);
        linea.setOrden(1);
        linea.setModalidad(ModalidadTrabajo.REPETICIONES);
        linea.setRepeticiones(8);
        linea.setPorcentajeRm(new BigDecimal("80.0"));
        linea.setPesoCalculado(new BigDecimal("80.00"));
        modulo.agregarEjercicio(linea);
        rutina.agregarModulo(modulo);
        return rutina;
    }

    private RutinaRequest solicitud(BigDecimal porcentaje, int rondas) {
        return new RutinaRequest(
                3L,
                "Fuerza octubre",
                "Fuerza",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                null,
                List.of(new ModuloRutinaRequest(
                        "Módulo 1",
                        rondas,
                        0,
                        90,
                        List.of(new EjercicioRutinaRequest(
                                4L,
                                ModalidadTrabajo.REPETICIONES,
                                8,
                                null,
                                null,
                                null,
                                porcentaje
                        ))
                ))
        );
    }
}
