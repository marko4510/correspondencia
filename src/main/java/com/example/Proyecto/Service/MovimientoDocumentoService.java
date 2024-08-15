package com.example.Proyecto.Service;
import java.util.List;

import com.example.Proyecto.Model.MovimientoDocumento;

public interface MovimientoDocumentoService {
    List<MovimientoDocumento> findAll();
    MovimientoDocumento findById(Long id);
    void save(MovimientoDocumento movimientoDocumento);
    void deleteById(Long id);
    public List<MovimientoDocumento> obtener_Flujo_Documento(String nroRuta);
}