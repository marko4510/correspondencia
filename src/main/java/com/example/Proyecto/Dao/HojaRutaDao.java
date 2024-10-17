package com.example.Proyecto.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;

public interface HojaRutaDao extends JpaRepository<HojaRuta, Long> {

    @Query(value = "SELECT hr.* FROM hoja_ruta hr  WHERE hr.unidad_registro = ?1 AND TO_CHAR(hr.fecha_creacion, 'YYYY') = ?2", nativeQuery = true)
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestion(Integer unidad_origen, String gestion);

    @Query(value = "select hr.* from hoja_ruta hr \n" + //
            "left join documento d on d.id_documento = hr.documento_id_documento \n" + //
            "where d.id_documento = ?1 ", nativeQuery = true)
    public List<HojaRuta> obtenerHojasDeRutaPorDocumento(Integer id_documento);

    @Query(value = "SELECT hr.* FROM hoja_ruta hr \n" + //
            "WHERE hr.nro_ruta = ?1 AND hr.unidad_registro = ?2 AND TO_CHAR(hr.fecha_creacion, 'YYYY') = ?3", nativeQuery = true)
    public HojaRuta obtenerHojaRutaPorGestionUnidad(Integer nro_ruta, Integer unidad_origen, String gestion);

    @Query(value = "SELECT hr.* FROM hoja_ruta hr  WHERE hr.unidad_registro = ?1 AND TO_CHAR(hr.fecha_creacion, 'YYYY') = ?2 AND hr.tipo_derivacion = ?3", nativeQuery = true)
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestionYTipoDerivacion(Integer unidad_origen, String gestion, String derivacion);

    @Query(value = "select * from hoja_ruta hr \n" + //
                        "where hr.unidad_registro = ?1 and TO_CHAR(hr.fecha_creacion, 'YYYY') = ?2",nativeQuery = true)
    public List<HojaRuta> ObtenerHojasDeRutaPorUnidadyGestion(Integer unidad_origen, String gestion);

}
