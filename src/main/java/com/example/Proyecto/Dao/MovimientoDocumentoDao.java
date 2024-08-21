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

    // @Query(value = "SELECT md.* FROM documento d \n" + //
    //             "LEFT JOIN movimiento_documento md ON md.id_documento = d.id_documento \n" + //
    //             "WHERE d.nro_ruta = ?1 AND d.unidad_origen = ?2 AND TO_CHAR(d.fecha_creacion, 'YYYY') = ?3",nativeQuery = true)
    // public List<MovimientoDocumento> obtener_Flujos_Documentos(String nroRuta, Integer unidad_origen, String gestion);
    
    @Query(value = "SELECT md.* FROM documento d \n" + //
                "LEFT JOIN movimiento_documento md ON md.id_documento = d.id_documento \n" + //
                "LEFT JOIN hoja_ruta hr ON hr.documento_id_documento = d.id_documento \n" + //
                "WHERE hr.nro_ruta = ?1 AND d.unidad_origen = ?2 AND TO_CHAR(d.fecha_creacion, 'YYYY') = ?3",nativeQuery = true)
    public List<MovimientoDocumento> obtener_Flujos_Documentos(String nroRuta, Integer unidad_origen, String gestion);
}
