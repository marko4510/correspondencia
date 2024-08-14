package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.DocumentoDao;
import com.example.Proyecto.Model.Documento;

@Service
public class DocumentoServiceImpl implements DocumentoService{
    @Autowired
    private DocumentoDao dao;

    @Override
    public List<Documento> findAll() {
        return dao.findAll();
    }

    @Override
    public Documento findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void save(Documento documento) {
        dao.save(documento);
    }

    @Override
    public void deleteById(Long id) {
        Documento documento = dao.findById(id).orElse(null);
        documento.setEstado("X");
        dao.save(documento);
    }
    

}
