package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Model.Documento;

public interface DocumentoDao extends JpaRepository<Documento, Long>{
    
}
