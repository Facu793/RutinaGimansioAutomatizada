package com.gestionrutina.alumno.repository;

import com.gestionrutina.alumno.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    List<Alumno> findByActivoTrueOrderByApellidoAscNombreAscIdAsc();

    List<Alumno> findAllByOrderByApellidoAscNombreAscIdAsc();

    @Query(value = """
            SELECT *
            FROM alumno
            WHERE (CAST(:incluirInactivos AS boolean) OR activo = TRUE)
              AND translate(lower(nombre || ' ' || apellido), 'áéíóúüñ', 'aeiouun')
                  LIKE '%' || :texto || '%'
            ORDER BY apellido ASC, nombre ASC, id ASC
            """, nativeQuery = true)
    List<Alumno> buscarPorNombre(@Param("texto") String texto, @Param("incluirInactivos") boolean incluirInactivos);
}
