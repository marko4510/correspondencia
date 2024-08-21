package com.example.Proyecto.Controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.PersonaService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private UnidadService unidadService;

     @GetMapping("/inicio")
    public String inicioUsuario(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {
        
            return "usuario/ventana";
    } else {
        return "redirect:/login";
    }
    }

    @PostMapping("/tablaRegistros")
    public String tablaRegistros(Model model, HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/tablaRegistros";
    }

    @GetMapping("/formularioM")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        return "usuario/formulario";
    }

    @PostMapping("/formulario/{id_usuario}")
    public String formulario(Model model, @PathVariable("id_usuario") Long id) {
        model.addAttribute("usuario", usuarioService.findById(id));
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("edit", "true");
        return "usuario/formulario";
    }

     @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@Validated Usuario usuario, HttpServletRequest request) {
        try {
            
      
            usuarioService.save(usuario);
            return ResponseEntity.ok("Registrado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    @PostMapping("/modificar")
    public ResponseEntity<String> modificar(@Validated Usuario usu) {
        Usuario usuario = usuarioService.findById(usu.getId_usuario());
        try {
            usuario.setUsuario_nom(usu.getUsuario_nom());
            usuario.setContrasena(usu.getContrasena());
            usuario.setUnidad(usu.getUnidad());
            usuario.setEstado(usu.getEstado());
            usuarioService.save(usuario);
            return ResponseEntity.ok("Modificado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }



}
