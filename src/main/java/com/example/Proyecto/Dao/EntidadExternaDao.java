package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.example.Proyecto.Model.EntidadExterna;


public interface EntidadExternaDao extends JpaRepository<EntidadExterna, Long>{
    
    
}
