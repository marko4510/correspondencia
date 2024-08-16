package com.example.Proyecto.Service;

import java.util.List;

import com.example.Proyecto.Model.Persona;


public interface PersonaService {
    List<Persona> findAll();
    Persona findById(Long id);
    void save(Persona persona);
    void deleteById(Long id);
    public Persona obtener_persona(String ci);


    
}
