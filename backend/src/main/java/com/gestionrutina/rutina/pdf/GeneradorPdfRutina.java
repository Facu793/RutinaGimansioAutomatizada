package com.gestionrutina.rutina.pdf;

import com.gestionrutina.rutina.dto.EjercicioRutinaResponse;
import com.gestionrutina.rutina.dto.ModuloRutinaResponse;
import com.gestionrutina.rutina.dto.RutinaResponse;
import com.gestionrutina.rutina.entity.UnidadDistancia;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class GeneradorPdfRutina {

    private static final Color NEGRO = new Color(0, 0, 0);
    private static final Color VERDE = new Color(200, 255, 0);
    private static final Color BLANCO = new Color(255, 255, 255);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_ARCHIVO = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final Clock clock;

    public GeneradorPdfRutina(Clock clock) {
        this.clock = clock;
    }

    public PdfRutina generar(RutinaResponse rutina) {
        LocalDate hoy = LocalDate.now(clock);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(documento, salida);
        documento.open();
        documento.add(encabezado(hoy));
        documento.add(regla());
        documento.add(titulo(rutina.nombre()));
        documento.add(datos(rutina, hoy));
        for (ModuloRutinaResponse modulo : ordenarModulos(rutina.modulos())) {
            documento.add(modulo(modulo));
        }
        documento.add(observaciones(rutina.observaciones()));
        documento.close();
        return new PdfRutina(salida.toByteArray(), nombreArchivo(rutina.alumnoNombre(), rutina.alumnoApellido(), hoy));
    }

    static String nombreArchivo(String nombre, String apellido, LocalDate fecha) {
        return parte(nombre) + "_" + parte(apellido) + "_Rutina_" + fecha.format(FECHA_ARCHIVO) + ".pdf";
    }

    private static String parte(String valor) {
        String sinAcentos = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        String seguro = sinAcentos.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return seguro.isBlank() ? "Alumno" : seguro;
    }

    private PdfPTable encabezado(LocalDate hoy) {
        PdfPTable tabla = new PdfPTable(new float[] { 46, 340, 140 });
        tabla.setWidthPercentage(100);
        PdfPCell logo = celda("GR", fuente(13, Font.BOLD, NEGRO), VERDE, Rectangle.NO_BORDER);
        logo.setHorizontalAlignment(Element.ALIGN_CENTER);
        logo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logo.setFixedHeight(36);
        tabla.addCell(logo);
        PdfPCell marca = celda("Gestión de rutinas", fuente(14, Font.BOLD, NEGRO), BLANCO, Rectangle.NO_BORDER);
        marca.setVerticalAlignment(Element.ALIGN_MIDDLE);
        marca.setPaddingLeft(10);
        tabla.addCell(marca);
        PdfPCell fecha = celda(hoy.format(FECHA), fuente(11, Font.NORMAL, NEGRO), BLANCO, Rectangle.NO_BORDER);
        fecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
        fecha.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabla.addCell(fecha);
        return tabla;
    }

    private PdfPTable regla() {
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        linea.setSpacingBefore(8);
        linea.setSpacingAfter(12);
        PdfPCell celda = new PdfPCell(new Phrase(""));
        celda.setFixedHeight(4);
        celda.setBackgroundColor(VERDE);
        celda.setBorder(Rectangle.NO_BORDER);
        linea.addCell(celda);
        return linea;
    }

    private Paragraph titulo(String nombre) {
        Paragraph titulo = new Paragraph(nombre, fuente(20, Font.BOLD, NEGRO));
        titulo.setSpacingAfter(10);
        return titulo;
    }

    private PdfPTable datos(RutinaResponse rutina, LocalDate hoy) {
        PdfPTable tabla = new PdfPTable(new float[] { 110, 400 });
        tabla.setWidthPercentage(100);
        fila(tabla, "Alumno", rutina.alumnoNombre() + " " + rutina.alumnoApellido());
        fila(tabla, "Edad", rutina.edad() + " años");
        fila(tabla, "Objetivo", rutina.objetivo());
        fila(tabla, "Fecha", hoy.format(FECHA));
        fila(tabla, "Vigencia", rutina.fechaInicio().format(FECHA) + " al " + rutina.fechaFin().format(FECHA));
        return tabla;
    }

    private void fila(PdfPTable tabla, String etiqueta, String valor) {
        tabla.addCell(celda(etiqueta, fuente(11, Font.BOLD, NEGRO), BLANCO, Rectangle.NO_BORDER));
        tabla.addCell(celda(valor, fuente(11, Font.NORMAL, NEGRO), BLANCO, Rectangle.NO_BORDER));
    }

    private PdfPTable modulo(ModuloRutinaResponse modulo) {
        PdfPTable bloque = new PdfPTable(1);
        bloque.setWidthPercentage(100);
        bloque.setSpacingBefore(16);
        bloque.setSplitLate(false);

        PdfPTable barra = new PdfPTable(new float[] { 3, 1 });
        barra.setWidthPercentage(100);
        PdfPCell nombre = celda(modulo.nombre(), fuente(12, Font.BOLD, BLANCO), NEGRO, Rectangle.NO_BORDER);
        nombre.setPadding(8);
        PdfPCell rondas = celda(textoRondas(modulo.rondas()), fuente(12, Font.BOLD, BLANCO), NEGRO, Rectangle.NO_BORDER);
        rondas.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rondas.setPadding(8);
        barra.addCell(nombre);
        barra.addCell(rondas);
        bloque.addCell(sinBorde(barra));

        String descansos = "Descanso entre ejercicios: " + tiempo(modulo.descansoEjercicioSegundos())
                + ". Entre rondas: " + tiempo(modulo.descansoRondaSegundos()) + ".";
        PdfPCell ayuda = celda(descansos, fuente(10, Font.NORMAL, NEGRO), BLANCO, Rectangle.NO_BORDER);
        ayuda.setPaddingTop(6);
        ayuda.setPaddingBottom(6);
        bloque.addCell(ayuda);
        bloque.addCell(sinBorde(ejercicios(modulo)));
        return bloque;
    }

    private PdfPTable ejercicios(ModuloRutinaResponse modulo) {
        PdfPTable tabla = new PdfPTable(new float[] { 28, 180, 90, 210 });
        tabla.setWidthPercentage(100);
        tabla.addCell(encabezadoTabla("#"));
        tabla.addCell(encabezadoTabla("Ejercicio"));
        tabla.addCell(encabezadoTabla("Categoría"));
        tabla.addCell(encabezadoTabla("Trabajo"));
        for (EjercicioRutinaResponse ejercicio : ordenarEjercicios(modulo.ejercicios())) {
            tabla.addCell(dato(String.valueOf(ejercicio.orden())));
            tabla.addCell(dato(ejercicio.ejercicioNombre()));
            tabla.addCell(dato(ejercicio.categoriaNombre()));
            tabla.addCell(dato(trabajo(ejercicio)));
        }
        return tabla;
    }

    private Paragraph observaciones(String texto) {
        Paragraph bloque = new Paragraph();
        bloque.setSpacingBefore(16);
        bloque.add(new Phrase("Observaciones\n", fuente(12, Font.BOLD, NEGRO)));
        bloque.add(new Phrase(texto == null || texto.isBlank() ? "Sin observaciones" : texto, fuente(11, Font.NORMAL, NEGRO)));
        return bloque;
    }

    private String trabajo(EjercicioRutinaResponse ejercicio) {
        return switch (ejercicio.modalidad()) {
            case REPETICIONES -> repeticiones(ejercicio);
            case TIEMPO -> tiempo(ejercicio.duracionSegundos());
            case DISTANCIA -> distancia(ejercicio);
        };
    }

    private String repeticiones(EjercicioRutinaResponse ejercicio) {
        String texto = ejercicio.repeticiones() + " repeticiones";
        if (ejercicio.porcentajeRm() != null) {
            texto += " · " + numero(ejercicio.porcentajeRm()) + "%";
        }
        if (ejercicio.pesoCalculado() != null) {
            texto += " · " + numero(ejercicio.pesoCalculado()) + " kg";
        }
        return texto;
    }

    private String distancia(EjercicioRutinaResponse ejercicio) {
        String unidad = ejercicio.unidadDistancia() == UnidadDistancia.KILOMETROS ? "km" : "m";
        return numero(ejercicio.distancia()) + " " + unidad;
    }

    private String tiempo(Integer segundos) {
        int total = segundos == null ? 0 : segundos;
        int minutos = total / 60;
        int resto = total % 60;
        if (minutos == 0) {
            return resto + " seg";
        }
        if (resto == 0) {
            return minutos == 1 ? "1 min" : minutos + " min";
        }
        return minutos + " min " + resto + " seg";
    }

    private String textoRondas(int rondas) {
        return rondas == 1 ? "1 ronda" : rondas + " rondas";
    }

    private String numero(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    private PdfPCell encabezadoTabla(String texto) {
        PdfPCell celda = celda(texto, fuente(10, Font.BOLD, NEGRO), VERDE, Rectangle.BOX);
        celda.setPadding(6);
        return celda;
    }

    private PdfPCell dato(String texto) {
        PdfPCell celda = celda(texto, fuente(10, Font.NORMAL, NEGRO), BLANCO, Rectangle.BOX);
        celda.setPadding(6);
        return celda;
    }

    private PdfPCell sinBorde(PdfPTable tabla) {
        PdfPCell celda = new PdfPCell(tabla);
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(0);
        return celda;
    }

    private PdfPCell celda(String texto, Font fuente, Color fondo, int borde) {
        PdfPCell celda = new PdfPCell(new Phrase(texto == null ? "" : texto, fuente));
        celda.setBackgroundColor(fondo);
        celda.setBorder(borde);
        celda.setBorderColor(NEGRO);
        celda.setPadding(3);
        return celda;
    }

    private Font fuente(float tamano, int estilo, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, "Cp1252", true, tamano, estilo, color);
    }

    private List<ModuloRutinaResponse> ordenarModulos(List<ModuloRutinaResponse> modulos) {
        return modulos.stream().sorted(Comparator.comparingInt(ModuloRutinaResponse::orden)).toList();
    }

    private List<EjercicioRutinaResponse> ordenarEjercicios(List<EjercicioRutinaResponse> ejercicios) {
        return ejercicios.stream().sorted(Comparator.comparingInt(EjercicioRutinaResponse::orden)).toList();
    }
}
