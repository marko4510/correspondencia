package com.example.Proyecto.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Proyecto.Model.Persona;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;

public interface PersonaDao extends JpaRepository<Persona, Long> {

    @Query(value = "SELECT p.* FROM persona p WHERE p.ci = ?1",nativeQuery = true)
    public Persona obtener_persona(String ci);

}
