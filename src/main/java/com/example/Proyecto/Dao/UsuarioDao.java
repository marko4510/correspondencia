package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Usuario;

public interface UsuarioDao extends JpaRepository<Usuario, Long>{
    
    @Query(value = "SELECT u.* FROM usuario u WHERE u.usuario_nom = ?1 AND u.contrasena = ?2",nativeQuery = true)
    public Usuario obtener_Usuario(String usuario_nom, String contrasena);
}
