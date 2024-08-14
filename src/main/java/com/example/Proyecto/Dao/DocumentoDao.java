package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.Usuario;

public interface DocumentoDao extends JpaRepository<Documento, Long>{



    @Query(value = "SELECT d.* FROM documento d WHERE d.nro_ruta = ?1",nativeQuery = true)
    public Documento obtener_documento_hojaRuta(String nroRuta);
}
