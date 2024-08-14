package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/seguimiento")
public class SeguimientoController {
    
    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {

            return "seguimiento/ventana";
        }else{
            return "redirect:/login";
        }

    }

}
