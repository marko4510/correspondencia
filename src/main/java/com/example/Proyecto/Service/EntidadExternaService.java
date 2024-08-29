package com.example.Proyecto.Service;
import java.util.List;

import com.example.Proyecto.Model.EntidadExterna;



public interface EntidadExternaService {

    List<EntidadExterna> findAll();
    EntidadExterna findById(Long id);
    void save(EntidadExterna entidadExterna);
    void deleteById(Long id);
    public EntidadExterna buscarPorNombre(String nombre);
   
}
