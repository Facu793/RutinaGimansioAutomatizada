package com.gestionrutina.plantilla.repository;

import com.gestionrutina.plantilla.entity.Plantilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

    List<Plantilla> findByActivaTrueOrderByNombreAscIdAsc();

    List<Plantilla> findAllByOrderByNombreAscIdAsc();

    @Query(value = """
            SELECT *
            FROM plantilla
            WHERE (CAST(:incluirInactivas AS boolean) OR activa = TRUE)
              AND translate(lower(nombre), 'áéíóúüñ', 'aeiouun') LIKE '%' || :texto || '%'
            ORDER BY nombre ASC, id ASC
            """, nativeQuery = true)
    List<Plantilla> buscarPorNombre(@Param("texto") String texto, @Param("incluirInactivas") boolean incluirInactivas);

    @Query(value = """
            SELECT COUNT(*)
            FROM plantilla
            WHERE activa = TRUE
              AND id <> COALESCE(CAST(:excluirId AS bigint), 0)
              AND translate(lower(nombre), 'áéíóúüñ', 'aeiouun') = :texto
            """, nativeQuery = true)
    long contarActivasConNombre(@Param("texto") String texto, @Param("excluirId") Long excluirId);
}
