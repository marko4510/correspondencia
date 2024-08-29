package com.example.Proyecto.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.Model.EntidadExterna;
import com.example.Proyecto.Service.EntidadExternaService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/entidadExterna")
public class EntidadExternaController {

    @Autowired
    private EntidadExternaService entidadExternaService;

    @PostMapping("/entidadExternaForm")
    public ResponseEntity<String> entidadExternaForm(@RequestParam("nom_externo")String nom_externo) {
        
        EntidadExterna entidadExterna = new EntidadExterna();

        entidadExterna.setEstado("A");
        entidadExterna.setNombre(nom_externo);
        entidadExternaService.save(entidadExterna);
        
        return ResponseEntity.ok("ok");
    }
    
}
