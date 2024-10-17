package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.DocumentoDao;
import com.example.Proyecto.Dao.HojaRutaDao;
import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.HojaRuta;

@Service
public class HojaRutaServiceImpl implements HojaRutaService{

    @Autowired
    private HojaRutaDao dao;

    @Override
    public List<HojaRuta> findAll() {
        return dao.findAll();
    }

    @Override
    public HojaRuta findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void save(HojaRuta hojaRuta) {
        dao.save(hojaRuta);
    }

    @Override
    public void deleteById(Long id) {
        HojaRuta hojaRuta = dao.findById(id).orElse(null);
     
        hojaRuta.setEstado("X");
        dao.save(hojaRuta);
    }

    @Override
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestion(Integer unidad_origen, String gestion) {
        return dao.obtenerHojasDeRutaPorUnidadYGestion(unidad_origen, gestion);
    }

    @Override
    public List<HojaRuta> obtenerHojasDeRutaPorDocumento(Integer id_documento) {
        return dao.obtenerHojasDeRutaPorDocumento(id_documento);
    }

    @Override
    public HojaRuta obtenerHojaRutaPorGestionUnidad(Integer nro_ruta, Integer unidad_origen, String gestion) {
        return dao.obtenerHojaRutaPorGestionUnidad(nro_ruta, unidad_origen, gestion);
    }

    @Override
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestionYTipoDerivacion(Integer unidad_origen, String gestion,
            String derivacion) {
        return dao.obtenerHojasDeRutaPorUnidadYGestionYTipoDerivacion(unidad_origen, gestion, derivacion);
    }

    @Override
    public List<HojaRuta> ObtenerHojasDeRutaPorUnidadyGestion(Integer unidad_origen, String gestion) {
        // TODO Auto-generated method stub
        return dao.ObtenerHojasDeRutaPorUnidadyGestion(unidad_origen, gestion);
    }


}
