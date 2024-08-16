package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.Proyecto.Dao.PersonaDao;

import com.example.Proyecto.Model.Persona;
import com.example.Proyecto.Model.Unidad;

@Service
public class PersonaServiceImpl implements PersonaService{
    @Autowired
    private PersonaDao dao;

    @Override
    public List<Persona> findAll() {
        return dao.findAll();
    }

    @Override
    public Persona findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void save(Persona persona) {
        dao.save(persona);
    }

    @Override
    public void deleteById(Long id) {
        Persona persona = dao.findById(id).orElse(null);
        persona.setEstado("X");
        dao.save(persona);
    }

    @Override
    public Persona obtener_persona(String ci) {
       return dao.obtener_persona(ci);
    }

   

 

 



}
