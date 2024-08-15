package com.example.Proyecto.Service;

import java.util.List;

import com.example.Proyecto.Model.Unidad;

public interface UnidadService {
    List<Unidad> findAll();
    Unidad findById(Long id);
    void save(Unidad documento);
    void deleteById(Long id);


    
}
