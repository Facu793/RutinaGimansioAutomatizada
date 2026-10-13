package com.gestionrutina.rutina.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modulo_rutina")
public class ModuloRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false)
    private int orden;

    @Column(nullable = false)
    private int rondas;

    @Column(name = "descanso_ejercicio_segundos", nullable = false)
    private int descansoEjercicioSegundos;

    @Column(name = "descanso_ronda_segundos", nullable = false)
    private int descansoRondaSegundos;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<EjercicioRutina> ejercicios = new ArrayList<>();

    public ModuloRutina() {
    }

    public void agregarEjercicio(EjercicioRutina ejercicio) {
        ejercicios.add(ejercicio);
        ejercicio.setModulo(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public int getRondas() {
        return rondas;
    }

    public void setRondas(int rondas) {
        this.rondas = rondas;
    }

    public int getDescansoEjercicioSegundos() {
        return descansoEjercicioSegundos;
    }

    public void setDescansoEjercicioSegundos(int descansoEjercicioSegundos) {
        this.descansoEjercicioSegundos = descansoEjercicioSegundos;
    }

    public int getDescansoRondaSegundos() {
        return descansoRondaSegundos;
    }

    public void setDescansoRondaSegundos(int descansoRondaSegundos) {
        this.descansoRondaSegundos = descansoRondaSegundos;
    }

    public List<EjercicioRutina> getEjercicios() {
        return ejercicios;
    }
}
