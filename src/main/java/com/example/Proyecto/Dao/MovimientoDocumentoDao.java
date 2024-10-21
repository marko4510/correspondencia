package com.example.Proyecto.Dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.MovimientoDocumento;

public interface MovimientoDocumentoDao extends JpaRepository<MovimientoDocumento, Long> {

    @Query(value = "SELECT md.* FROM documento d \n" + //
            "LEFT JOIN movimiento_documento md ON md.id_documento = d.id_documento \n" + //
            "WHERE d.nro_ruta = ?1", nativeQuery = true)
    public List<MovimientoDocumento> obtener_Flujo_Documento(String nroRuta);

    // @Query(value = "SELECT md.* FROM documento d \n" + //
    // "LEFT JOIN movimiento_documento md ON md.id_documento = d.id_documento \n" +
    // //
    // "WHERE d.nro_ruta = ?1 AND d.unidad_origen = ?2 AND TO_CHAR(d.fecha_creacion,
    // 'YYYY') = ?3",nativeQuery = true)
    // public List<MovimientoDocumento> obtener_Flujos_Documentos(String nroRuta,
    // Integer unidad_origen, String gestion);

    // @Query(value = "SELECT md.* FROM documento d \n" + //
    // "LEFT JOIN movimiento_documento md ON md.id_documento = d.id_documento \n" +
    // //
    // "LEFT JOIN hoja_ruta hr ON hr.documento_id_documento = d.id_documento \n" +
    // //
    // "WHERE hr.nro_ruta = ?1 AND d.unidad_origen = ?2 AND
    // TO_CHAR(d.fecha_creacion, 'YYYY') = ?3",nativeQuery = true)
    // public List<MovimientoDocumento> obtener_Flujos_Documentos(String nroRuta,
    // Integer unidad_origen, String gestion);

    @Query(value = "SELECT * FROM movimiento_documento md \n" + //
            "LEFT JOIN hoja_ruta hr ON hr.id_hoja_ruta = md.id_hoja_ruta\n" + //
            "WHERE hr.nro_ruta = ?1 AND hr.unidad_registro = ?2 AND TO_CHAR(hr.fecha_creacion , 'YYYY') = ?3 ", nativeQuery = true)
    public List<MovimientoDocumento> obtener_Flujos_Documentos(Integer nroRuta, Integer unidad_origen, String gestion);

    @Query(value = "SELECT * FROM movimiento_documento md WHERE estado_movimiento = 'P' AND md.id_unidad_destino = ?1", nativeQuery = true)
    public List<MovimientoDocumento> ListaMovimientosSolicitados(Integer unidad_destino);

    @Query(value = "SELECT md.* FROM movimiento_documento md WHERE md.estado_movimiento = 'HA' AND md.id_unidad_origen = ?1 AND TO_CHAR(md.fecha_hora_registro , 'YYYY') = ?2", nativeQuery = true)
    public List<MovimientoDocumento> Lista_Archivados(Long unidad_origen, String gestion);

    
}
