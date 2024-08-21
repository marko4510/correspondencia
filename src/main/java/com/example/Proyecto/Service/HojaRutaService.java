package com.example.Proyecto.Service;

import java.util.List;


import com.example.Proyecto.Model.HojaRuta;

public interface HojaRutaService {
    List<HojaRuta> findAll();
    HojaRuta findById(Long id);
    void save(HojaRuta hojaRuta);
    void deleteById(Long id);
    public List<HojaRuta> obtenerHojasDeRutaPorUnidadYGestion(Integer unidad_origen, String gestion);
    
}