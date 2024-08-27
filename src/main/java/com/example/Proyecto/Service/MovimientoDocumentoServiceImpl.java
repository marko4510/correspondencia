package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.MovimientoDocumentoDao;
import com.example.Proyecto.Model.MovimientoDocumento;

@Service
public class MovimientoDocumentoServiceImpl implements MovimientoDocumentoService{

    @Autowired
    private MovimientoDocumentoDao movimientoDocumentoDao;

    @Override
    public List<MovimientoDocumento> findAll() {
        // TODO Auto-generated method stub
        return movimientoDocumentoDao.findAll();
    }

    @Override
    public MovimientoDocumento findById(Long id) {
        // TODO Auto-generated method stub
        return movimientoDocumentoDao.findById(id).orElse(null);
    }

    @Override
    public void save(MovimientoDocumento movimientoDocumento) {
        // TODO Auto-generated method stub
        movimientoDocumentoDao.save(movimientoDocumento);
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        movimientoDocumentoDao.deleteById(id);
    }

    @Override
    public List<MovimientoDocumento> obtener_Flujo_Documento(String nroRuta) {
        // TODO Auto-generated method stub
        return movimientoDocumentoDao.obtener_Flujo_Documento(nroRuta);
    }

    @Override
    public List<MovimientoDocumento> obtener_Flujos_Documentos(Integer nroRuta, Integer unidad_origen, String gestion) {
        // TODO Auto-generated method stub
        return movimientoDocumentoDao.obtener_Flujos_Documentos(nroRuta, unidad_origen, gestion);
    }
    
}
