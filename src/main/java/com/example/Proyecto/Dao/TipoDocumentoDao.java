package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Model.TipoDocumento;

public interface TipoDocumentoDao extends JpaRepository<TipoDocumento, Long>{
    
}
