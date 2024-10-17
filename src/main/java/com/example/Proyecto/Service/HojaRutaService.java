package com.example.Proyecto.Service;

import java.util.List;


import com.example.Proyecto.Model.HojaRuta;

public interface HojaRutaService {
    List<HojaRuta> findAll();
    HojaRuta findById(Long id);
    void save(HojaRuta hojaRuta);
    void deleteById(Long id);
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestion(Integer unidad_origen, String gestion);
    public List<HojaRuta> obtenerHojasDeRutaPorDocumento(Integer id_documento);
    public HojaRuta obtenerHojaRutaPorGestionUnidad(Integer nro_ruta, Integer unidad_origen, String gestion);
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestionYTipoDerivacion(Integer unidad_origen, String gestion, String derivacion);
    public List<HojaRuta> ObtenerHojasDeRutaPorUnidadyGestion(Integer unidad_origen, String gestion);

}
