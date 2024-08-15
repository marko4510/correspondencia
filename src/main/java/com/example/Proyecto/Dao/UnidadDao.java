package com.example.Proyecto.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Proyecto.Model.Unidad;

public interface UnidadDao extends JpaRepository<Unidad, Long> {

    @Query(value = "SELECT u.* FROM unidad u WHERE u.id_unidad NOT IN " +
            "(SELECT unidad_id FROM usuario WHERE id_usuario = :idUsuario)", nativeQuery = true)
    List<Unidad> findUnidadesNoRelacionadasConUsuario(@Param("idUsuario") Long idUsuario);

}
