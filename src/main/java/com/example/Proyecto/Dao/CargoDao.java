package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Cargo;
import com.example.Proyecto.Model.Unidad;

public interface CargoDao extends JpaRepository<Cargo, Long>{
    
    @Query(value = "SELECT c.* FROM cargo c \n" + //
                "WHERE c.nombre = ?1 ",nativeQuery = true)
    public Cargo obtener_cargoPorNombre(String nombreCargo);
}
