package com.gestionrutina.rutina.pdf;

import com.gestionrutina.rutina.dto.EjercicioRutinaResponse;
import com.gestionrutina.rutina.dto.ModuloRutinaResponse;
import com.gestionrutina.rutina.dto.RutinaResponse;
import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import com.gestionrutina.rutina.entity.UnidadDistancia;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneradorPdfRutinaTest {

    @Test
    void armaElNombreConAlumnoYFechaDeGeneracion() {
        assertEquals(
                "Juan_Perez_Rutina_07-10-2026.pdf",
                GeneradorPdfRutina.nombreArchivo("Juan", "Pérez", LocalDate.of(2026, 10, 7))
        );
    }

    @Test
    void elPdfIncluyeLosDatosDeLaRutina() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-07T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        PdfRutina pdf = new GeneradorPdfRutina(clock).generar(rutina());

        assertEquals("Juan_Perez_Rutina_07-10-2026.pdf", pdf.nombreArchivo());
        assertTrue(new String(pdf.contenido(), 0, 4).startsWith("%PDF"));

        PdfReader lector = new PdfReader(pdf.contenido());
        String texto = new PdfTextExtractor(lector).getTextFromPage(1);
        lector.close();

        assertTrue(texto.contains("Juan Pérez"));
        assertTrue(texto.contains("36 años"));
        assertTrue(texto.contains("Fuerza"));
        assertTrue(texto.contains("07/10/2026"));
        assertTrue(texto.contains("01/10/2026 al 31/10/2026"));
        assertTrue(texto.contains("Módulo 1"));
        assertTrue(texto.contains("3 rondas"));
        assertTrue(texto.contains("0 seg"));
        assertTrue(texto.contains("1 min 30 seg"));
        assertTrue(texto.contains("Press banca"));
        assertTrue(texto.contains("8 repeticiones"));
        assertTrue(texto.contains("80%"));
        assertTrue(texto.contains("80 kg"));
        assertTrue(texto.contains("Plancha"));
        assertTrue(texto.contains("45 seg"));
        assertTrue(texto.contains("Cinta"));
        assertTrue(texto.contains("1 km"));
        assertTrue(texto.contains("Llevar agua"));
    }

    private RutinaResponse rutina() {
        return new RutinaResponse(
                9L,
                3L,
                "Juan",
                "Pérez",
                36,
                "Fuerza octubre",
                "Fuerza",
                LocalDate.of(2026, 10, 13),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                "Llevar agua",
                true,
                List.of(new ModuloRutinaResponse(
                        1L,
                        "Módulo 1",
                        1,
                        3,
                        0,
                        90,
                        List.of(
                                linea(1, "Press banca", "Pecho", ModalidadTrabajo.REPETICIONES, 8, null, null, null, new BigDecimal("80.0"), new BigDecimal("80.00")),
                                linea(2, "Plancha", "Abdominales", ModalidadTrabajo.TIEMPO, null, 45, null, null, null, null),
                                linea(3, "Cinta", "Cardio", ModalidadTrabajo.DISTANCIA, null, null, new BigDecimal("1.00"), UnidadDistancia.KILOMETROS, null, null)
                        )
                )),
                List.of()
        );
    }

    private EjercicioRutinaResponse linea(
            int orden,
            String nombre,
            String categoria,
            ModalidadTrabajo modalidad,
            Integer repeticiones,
            Integer duracion,
            BigDecimal distancia,
            UnidadDistancia unidad,
            BigDecimal porcentaje,
            BigDecimal peso
    ) {
        return new EjercicioRutinaResponse(
                (long) orden,
                orden,
                4L,
                nombre,
                categoria,
                true,
                modalidad,
                repeticiones,
                duracion,
                distancia,
                unidad,
                porcentaje,
                peso
        );
    }
}
