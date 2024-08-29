package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.CargoDao;
import com.example.Proyecto.Dao.EntidadExternaDao;
import com.example.Proyecto.Dao.TipoDocumentoDao;
import com.example.Proyecto.Model.Cargo;
import com.example.Proyecto.Model.EntidadExterna;
import com.example.Proyecto.Model.TipoDocumento;
import com.example.Proyecto.Model.Unidad;

@Service
public class EntidadExternaServiceImpl implements EntidadExternaService{

    @Autowired
    private EntidadExternaDao entidadExternaDao;

    @Override
    public List<EntidadExterna> findAll() {
        return entidadExternaDao.findAll();
    }

    @Override
    public EntidadExterna findById(Long id) {
        return entidadExternaDao.findById(id).orElse(null);
    }

    @Override
    public void save(EntidadExterna entidadExterna) {
        entidadExternaDao.save(entidadExterna);
    }

    @Override
    public void deleteById(Long id) {
        EntidadExterna entidadExterna = entidadExternaDao.findById(id).orElse(null);
        entidadExterna.setEstado("X");
        entidadExternaDao.save(entidadExterna);
    }

    @Override
    public EntidadExterna buscarPorNombre(String nombre) {
        return entidadExternaDao.buscarPorNombre(nombre);
    }
    
}
