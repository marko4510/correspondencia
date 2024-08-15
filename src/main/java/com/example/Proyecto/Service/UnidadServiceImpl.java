package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.DocumentoDao;
import com.example.Proyecto.Dao.UnidadDao;
import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.Unidad;

@Service
public class UnidadServiceImpl implements UnidadService{
    @Autowired
    private UnidadDao dao;

    @Override
    public List<Unidad> findAll() {
        return dao.findAll();
    }

    @Override
    public Unidad findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void save(Unidad unidad) {
        dao.save(unidad);
    }

    @Override
    public void deleteById(Long id) {
        Unidad unidad = dao.findById(id).orElse(null);
        unidad.setEstado("X");
        dao.save(unidad);
    }

    @Override
    public List<Unidad> findUnidadesNoRelacionadasConUsuario(Long idUsuario) {
       return dao.findUnidadesNoRelacionadasConUsuario(idUsuario);
    }



}
