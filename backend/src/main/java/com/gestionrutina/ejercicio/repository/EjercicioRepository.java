package com.gestionrutina.ejercicio.repository;

import com.gestionrutina.ejercicio.entity.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByActivoTrueOrderByNombreAscIdAsc();

    List<Ejercicio> findAllByOrderByNombreAscIdAsc();

    List<Ejercicio> findByCategoriaIdAndActivoTrueOrderByNombreAscIdAsc(Long categoriaId);

    List<Ejercicio> findByCategoriaIdOrderByNombreAscIdAsc(Long categoriaId);

    long countByCategoriaIdAndActivoTrue(Long categoriaId);

    @Query(value = """
            SELECT *
            FROM ejercicio
            WHERE (CAST(:incluirInactivos AS boolean) OR activo = TRUE)
              AND (CAST(:filtrarCategoria AS boolean) = FALSE OR categoria_id = :categoriaId)
              AND translate(lower(nombre), 'áéíóúüñ', 'aeiouun') LIKE '%' || :texto || '%'
            ORDER BY nombre ASC, id ASC
            """, nativeQuery = true)
    List<Ejercicio> buscarPorNombre(
            @Param("texto") String texto,
            @Param("categoriaId") Long categoriaId,
            @Param("filtrarCategoria") boolean filtrarCategoria,
            @Param("incluirInactivos") boolean incluirInactivos
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM ejercicio
            WHERE activo = TRUE
              AND categoria_id = :categoriaId
              AND id <> COALESCE(CAST(:excluirId AS bigint), 0)
              AND translate(lower(nombre), 'áéíóúüñ', 'aeiouun') = :texto
            """, nativeQuery = true)
    long contarActivosConNombre(
            @Param("categoriaId") Long categoriaId,
            @Param("texto") String texto,
            @Param("excluirId") Long excluirId
    );
}
