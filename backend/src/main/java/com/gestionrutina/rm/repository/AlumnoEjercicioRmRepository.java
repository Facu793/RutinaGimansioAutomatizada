package com.gestionrutina.rm.repository;

import com.gestionrutina.rm.entity.AlumnoEjercicioRm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlumnoEjercicioRmRepository extends JpaRepository<AlumnoEjercicioRm, Long> {

    @Query("""
            SELECT r
            FROM AlumnoEjercicioRm r
            JOIN FETCH r.ejercicio ejercicio
            JOIN FETCH ejercicio.categoria
            WHERE r.alumno.id = :alumnoId
            ORDER BY ejercicio.nombre ASC, r.id ASC
            """)
    List<AlumnoEjercicioRm> listarPorAlumno(@Param("alumnoId") Long alumnoId);

    Optional<AlumnoEjercicioRm> findByAlumnoIdAndEjercicioId(Long alumnoId, Long ejercicioId);
}
