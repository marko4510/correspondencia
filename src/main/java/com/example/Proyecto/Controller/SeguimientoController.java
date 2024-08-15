package com.example.Proyecto.Controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Service.MovimientoDocumentoService;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping(value = "/seguimiento")
public class SeguimientoController {
    
    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {

            return "seguimiento/ventana";
        }else{
            return "redirect:/login";
        }
    }
    

   @PostMapping("/buscar_documento/{nroRuta}")
        public String buscar_documento(@PathVariable(name = "nroRuta") String nroRuta, Model model) {
            List<MovimientoDocumento> flujoDocumentos = movimientoDocumentoService.obtener_Flujo_Documento(nroRuta);

            if (flujoDocumentos.size() > 0) {
                model.addAttribute("flujo", flujoDocumentos);
                return "seguimiento/flujoDocumento"; // Retorna la vista si hay resultados
            } else {
                return "seguimiento/noEncontrado"; // Retorna una vista alternativa si no hay resultados
            }
        }
    
}
