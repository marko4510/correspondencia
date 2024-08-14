package com.example.Proyecto.Service;

import java.util.List;

import com.example.Proyecto.Model.Documento;

public interface DocumentoService {
    List<Documento> findAll();
    Documento findById(Long id);
    void save(Documento documento);
    void deleteById(Long id);
    
}
