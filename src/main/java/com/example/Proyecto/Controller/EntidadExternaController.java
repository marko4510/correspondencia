package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import com.example.Proyecto.Model.EntidadExterna;
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.EntidadExternaService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/entidadExterna")
public class EntidadExternaController {

    @Autowired
    private EntidadExternaService entidadExternaService;

    @PostMapping("/entidadExternaForm")
    public ResponseEntity<String> entidadExternaForm(@RequestParam("nom_externo") String nom_externo) {
        if (entidadExternaService.buscarPorNombre(nom_externo) == null) {
            EntidadExterna entidadExterna = new EntidadExterna();

            entidadExterna.setEstado("A");
            entidadExterna.setNombre(nom_externo);
            entidadExternaService.save(entidadExterna);

            return ResponseEntity.ok("ok");
        } else {
            return ResponseEntity.ok("invalido");
        }
    }

    @PostMapping("/añadirOpcionSelectEntidadExterna/{nombre}")
    public ResponseEntity<String[]> cargarSelectUsuarios(@PathVariable("nombre") String nombre,
            HttpServletRequest request) {
        EntidadExterna entidadExterna = entidadExternaService.buscarPorNombre(nombre);

        String[] entidad = { entidadExterna.getId_entidad_externa().toString(), entidadExterna.getNombre() };

        return ResponseEntity.ok(entidad);
    }

}
