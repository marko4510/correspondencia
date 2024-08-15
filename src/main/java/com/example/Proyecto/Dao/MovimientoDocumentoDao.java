package com.example.Proyecto.Dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.MovimientoDocumento;

public interface MovimientoDocumentoDao extends JpaRepository<MovimientoDocumento, Long>{
    
    @Query(value = "SELECT md.* FROM documento d \n" + //
                "LEFT JOIN movimiento_documento md ON md.id_documento = d.id_documento \n" + //
                "WHERE d.nro_ruta = ?1",nativeQuery = true)
    public List<MovimientoDocumento> obtener_Flujo_Documento(String nroRuta);
}