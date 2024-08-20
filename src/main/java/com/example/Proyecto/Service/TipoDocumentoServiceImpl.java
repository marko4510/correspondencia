package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.TipoDocumentoDao;
import com.example.Proyecto.Model.TipoDocumento;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService{

    @Autowired
    private TipoDocumentoDao tipoDocumentoDao;

    @Override
    public List<TipoDocumento> findAll() {
        return tipoDocumentoDao.findAll();
    }

    @Override
    public TipoDocumento findById(Long id) {
        return tipoDocumentoDao.findById(id).orElse(null);
    }

    @Override
    public void save(TipoDocumento tipoDocumento) {
        tipoDocumentoDao.save(tipoDocumento);
    }

    @Override
    public void deleteById(Long id) {
        TipoDocumento tipoDocumento = tipoDocumentoDao.findById(id).orElse(null);
        tipoDocumento.setEstado("X");
        tipoDocumentoDao.save(tipoDocumento);
    }
    
}
