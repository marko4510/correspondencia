package com.example.Proyecto.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.Usuario;

public interface HojaRutaDao extends JpaRepository<HojaRuta, Long>{

    @Query(value = "SELECT hr.* FROM hoja_ruta hr  WHERE hr.unidad_reg = ?1 AND TO_CHAR(hr.fecha_creacion, 'YYYY') = ?2", nativeQuery = true)
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestion(Integer unidad_origen, String gestion);

  
    
}
