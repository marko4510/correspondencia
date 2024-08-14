package com.example.Proyecto.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/documento")
public class DocumentoController {
    @GetMapping("/inicio")
    public String inicio() {
        return "documento/ventana";
    }
    
}
