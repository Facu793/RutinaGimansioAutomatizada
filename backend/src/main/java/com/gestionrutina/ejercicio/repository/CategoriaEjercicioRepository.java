package com.gestionrutina.ejercicio.repository;

import com.gestionrutina.ejercicio.entity.CategoriaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaEjercicioRepository extends JpaRepository<CategoriaEjercicio, Long> {

    List<CategoriaEjercicio> findByActivoTrueOrderByNombreAscIdAsc();

    List<CategoriaEjercicio> findAllByOrderByNombreAscIdAsc();

    @Query(value = """
            SELECT *
            FROM categoria_ejercicio
            WHERE (CAST(:incluirInactivos AS boolean) OR activo = TRUE)
              AND translate(lower(nombre), 'áéíóúüñ', 'aeiouun') LIKE '%' || :texto || '%'
            ORDER BY nombre ASC, id ASC
            """, nativeQuery = true)
    List<CategoriaEjercicio> buscarPorNombre(@Param("texto") String texto, @Param("incluirInactivos") boolean incluirInactivos);

    @Query(value = """
            SELECT COUNT(*)
            FROM categoria_ejercicio
            WHERE activo = TRUE
              AND id <> COALESCE(CAST(:excluirId AS bigint), 0)
              AND translate(lower(nombre), 'áéíóúüñ', 'aeiouun') = :texto
            """, nativeQuery = true)
    long contarActivasConNombre(@Param("texto") String texto, @Param("excluirId") Long excluirId);
}
