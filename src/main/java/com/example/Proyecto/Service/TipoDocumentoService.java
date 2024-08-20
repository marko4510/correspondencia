package com.example.Proyecto.Service;
import java.util.List;

import com.example.Proyecto.Model.TipoDocumento;

public interface TipoDocumentoService {
    List<TipoDocumento> findAll();
    TipoDocumento findById(Long id);
    void save(TipoDocumento tipoDocumento);
    void deleteById(Long id);
}
