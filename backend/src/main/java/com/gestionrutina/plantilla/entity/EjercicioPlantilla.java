package com.gestionrutina.plantilla.entity;

import com.gestionrutina.ejercicio.entity.Ejercicio;
import com.gestionrutina.rutina.entity.ModalidadTrabajo;
import com.gestionrutina.rutina.entity.UnidadDistancia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "ejercicio_plantilla")
public class EjercicioPlantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private ModuloPlantilla modulo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private int orden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadTrabajo modalidad;

    private Integer repeticiones;

    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;

    @Column(precision = 8, scale = 2)
    private BigDecimal distancia;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_distancia", length = 20)
    private UnidadDistancia unidadDistancia;

    @Column(name = "porcentaje_rm", precision = 4, scale = 1)
    private BigDecimal porcentajeRm;

    public EjercicioPlantilla() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModuloPlantilla getModulo() {
        return modulo;
    }

    public void setModulo(ModuloPlantilla modulo) {
        this.modulo = modulo;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public ModalidadTrabajo getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadTrabajo modalidad) {
        this.modalidad = modalidad;
    }

    public Integer getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(Integer repeticiones) {
        this.repeticiones = repeticiones;
    }

    public Integer getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(Integer duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public BigDecimal getDistancia() {
        return distancia;
    }

    public void setDistancia(BigDecimal distancia) {
        this.distancia = distancia;
    }

    public UnidadDistancia getUnidadDistancia() {
        return unidadDistancia;
    }

    public void setUnidadDistancia(UnidadDistancia unidadDistancia) {
        this.unidadDistancia = unidadDistancia;
    }

    public BigDecimal getPorcentajeRm() {
        return porcentajeRm;
    }

    public void setPorcentajeRm(BigDecimal porcentajeRm) {
        this.porcentajeRm = porcentajeRm;
    }
}
