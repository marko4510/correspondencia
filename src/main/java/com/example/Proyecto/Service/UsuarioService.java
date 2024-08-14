package com.example.Proyecto.Service;

import java.util.List;

import com.example.Proyecto.Model.Usuario;

public interface UsuarioService {
    List<Usuario> findAll();
    Usuario findById(Long id);
    void save(Usuario usuario);
    void deleteById(Long id);
    public Usuario obtener_Usuario(String usuario_nom, String contrasena);
}
