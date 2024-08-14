package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.UsuarioDao;
import com.example.Proyecto.Model.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    public List<Usuario> findAll() {
        // TODO Auto-generated method stub
        return usuarioDao.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        // TODO Auto-generated method stub
        return usuarioDao.findById(id).orElse(null);
    }

    @Override
    public void save(Usuario usuario) {
        // TODO Auto-generated method stub
        usuarioDao.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        usuarioDao.deleteById(id);
    }

    @Override
    public Usuario obtener_Usuario(String usuario_nom, String contrasena) {
        // TODO Auto-generated method stub
        return usuarioDao.obtener_Usuario(usuario_nom, contrasena);
    }
    
}
