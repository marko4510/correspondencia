package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Service.DocumentoService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping(value = "/seguimiento")
public class SeguimientoController {
    
    @Autowired
    private DocumentoService documentoService;

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {

            return "seguimiento/ventana";
        }else{
            return "redirect:/login";
        }
    }
    

    @PostMapping("/buscar_documento/{nroRuta}")
    public String buscar_documento(@PathVariable(name = "nroRuta")String nroRuta,Model model) {
        
        model.addAttribute("flujo", documentoService.obtener_Flujo_Documento(nroRuta));
        
        return "seguimiento/flujoDocumento";
    }
    
}
