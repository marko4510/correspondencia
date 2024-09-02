package com.example.Proyecto.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Model.Usuario;
import java.util.List;

public interface UsuarioDao extends JpaRepository<Usuario, Long>{
    
    @Query(value = "SELECT u.* FROM usuario u WHERE u.usuario_nom = ?1 AND u.contrasena = ?2",nativeQuery = true)
    public Usuario obtener_Usuario(String usuario_nom, String contrasena);

    @Query(value = "SELECT u FROM Usuario u WHERE u.unidad.id_unidad =?1 AND u.id_usuario != ?2 AND u.estado != 'X'")
    public List<Usuario> listarUsuariosPorUnidad(Long idUnidad, Long idUsuario);

    @Query(value = "SELECT u.* FROM usuario u \n" + //
                "LEFT JOIN persona p ON p.id_persona = u.id_persona \n" + //
                "WHERE p.ci = ?1 ",nativeQuery = true)
    public Usuario obtener_Usuario_CiPersona(String ci);
}
