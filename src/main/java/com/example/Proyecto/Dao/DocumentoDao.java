package com.example.Proyecto.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.Usuario;

public interface DocumentoDao extends JpaRepository<Documento, Long>{


    @Query(value = "SELECT d.* FROM documento d WHERE d.nro_ruta = ?1",nativeQuery = true)
    public Documento obtener_documento_hojaRuta(String nroRuta);

    @Query(value = "SELECT d.* FROM documento d \n" + //
                "WHERE d.nro_ruta = ?1 AND d.unidad_origen = ?2 AND TO_CHAR(d.fecha_creacion, 'YYYY') = ?3",nativeQuery = true)
    public Documento obtener_DocumentosRutaGestionUnidad(String nroRuta, Integer unidad_origen, String gestion);

    @Query(value = "SELECT d.* FROM documento d \n" + //
    "WHERE  d.unidad_origen = ?1",nativeQuery = true)
    public List<Documento> obtener_DocumentosUnidad(Integer unidad_origen);

    
}
