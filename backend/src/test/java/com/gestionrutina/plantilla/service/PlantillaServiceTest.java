package com.gestionrutina.plantilla.service;

import com.gestionrutina.common.exception.ReglaDeNegocioException;
import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.ejercicio.repository.EjercicioRepository;
import com.gestionrutina.plantilla.dto.PlantillaRequest;
import com.gestionrutina.plantilla.dto.PlantillaResponse;
import com.gestionrutina.plantilla.entity.ModuloPlantilla;
import com.gestionrutina.plantilla.entity.Plantilla;
import com.gestionrutina.plantilla.repository.PlantillaRepository;
import com.gestionrutina.rutina.dto.EjercicioRutinaRequest;
import com.gestionrutina.rutina.dto.ModuloRutinaRequest;
import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlantillaServiceTest {

    @Mock
    private PlantillaRepository plantillaRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    private PlantillaService plantillaService;

    @BeforeEach
    void setUp() {
        plantillaService = new PlantillaService(plantillaRepository, ejercicioRepository);
    }

    @Test
    void guardaElPorcentajeSinCalcularPeso() {
        prepararEjercicio();
        when(plantillaRepository.contarActivasConNombre(eq("fuerza"), isNull())).thenReturn(0L);
        when(plantillaRepository.save(any(Plantilla.class))).thenAnswer(invocation -> {
            Plantilla plantilla = invocation.getArgument(0);
            plantilla.setId(8L);
            return plantilla;
        });

        PlantillaResponse respuesta = plantillaService.crear(solicitud("Fuerza", 4));

        assertEquals(8L, respuesta.id());
        assertEquals(new BigDecimal("80.0"), respuesta.modulos().getFirst().ejercicios().getFirst().porcentajeRm());
        assertEquals(4, respuesta.modulos().getFirst().rondas());
        assertTrue(respuesta.advertencias().isEmpty());
    }

    @Test
    void rechazaUnNombreActivoRepetido() {
        when(plantillaRepository.contarActivasConNombre(eq("fuerza"), isNull())).thenReturn(1L);

        assertThrows(ReglaDeNegocioException.class, () -> plantillaService.crear(solicitud("Fuerza", 3)));
    }

    @Test
    void editarLaPlantillaNoReescribeOtraInstancia() {
        prepararEjercicio();
        Plantilla original = plantillaConRondas(1L, 4);
        Plantilla editada = plantillaConRondas(2L, 4);
        when(plantillaRepository.findById(2L)).thenReturn(Optional.of(editada));
        when(plantillaRepository.contarActivasConNombre(eq("fuerza"), eq(2L))).thenReturn(0L);

        plantillaService.actualizar(2L, solicitud("Fuerza", 9));

        assertEquals(4, original.getModulos().getFirst().getRondas());
        assertEquals(9, editada.getModulos().getFirst().getRondas());
    }

    private void prepararEjercicio() {
        CategoriaEjercicio categoria = new CategoriaEjercicio();
        categoria.setNombre("Pecho");
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(4L);
        ejercicio.setNombre("Press banca");
        ejercicio.setCategoria(categoria);
        ejercicio.setActivo(true);
        when(ejercicioRepository.findById(4L)).thenReturn(Optional.of(ejercicio));
    }

    private Plantilla plantillaConRondas(Long id, int rondas) {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(id);
        plantilla.setNombre("Fuerza");
        plantilla.setObjetivo("Fuerza");
        plantilla.setActiva(true);
        plantilla.agregarModulo(modulo(rondas));
        return plantilla;
    }

    private ModuloPlantilla modulo(int rondas) {
        ModuloPlantilla modulo = new ModuloPlantilla();
        modulo.setNombre("Módulo 1");
        modulo.setOrden(1);
        modulo.setRondas(rondas);
        modulo.setDescansoEjercicioSegundos(0);
        modulo.setDescansoRondaSegundos(60);
        return modulo;
    }

    private PlantillaRequest solicitud(String nombre, int rondas) {
        return new PlantillaRequest(
                nombre,
                "Fuerza",
                null,
                List.of(new ModuloRutinaRequest(
                        "Módulo 1",
                        rondas,
                        0,
                        60,
                        List.of(new EjercicioRutinaRequest(
                                4L,
                                ModalidadTrabajo.REPETICIONES,
                                8,
                                null,
                                null,
                                null,
                                new BigDecimal("80")
                        ))
                ))
        );
    }
}
