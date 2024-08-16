package com.example.Proyecto.Service;

import java.util.List;

import com.example.Proyecto.Model.Documento;

public interface DocumentoService {
    List<Documento> findAll();
    Documento findById(Long id);
    void save(Documento documento);
    void deleteById(Long id);
    public Documento obtener_documento_hojaRuta(String nroRuta);
    public Documento obtener_DocumentosRutaGestionUnidad(String nroRuta, Integer unidad_origen, String gestion);
    public List<Documento> obtener_DocumentosUnidad(Integer unidad_origen);

}
