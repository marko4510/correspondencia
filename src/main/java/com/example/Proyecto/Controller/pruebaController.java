package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Service.DocumentoService;

@Controller
public class pruebaController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
	private SpringTemplateEngine templateEngine;

    @RequestMapping(value = "/prueba", method = RequestMethod.GET)
    public String prueba() {

        return "busqueda/prueba";
    }

    @PostMapping("/formularioDocumento")
	public ResponseEntity<?> formularioDocumento(@RequestParam(name = "nroRuta") String nroRuta, Model model, HttpServletRequest request,
			HttpServletResponse response) {

                Documento documento = documentoService.obtener_documento_hojaRuta(nroRuta);

                if (documento == null) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontró documento con el número de Hoja de Ruta proporcionado.");
                }

		model.addAttribute("documento", documento);

		WebContext context = new WebContext(request, response, request.getServletContext());
		context.setVariables(model.asMap());

		String htmlContent = templateEngine.process("busqueda/formulario", context);
		return ResponseEntity.ok().body(htmlContent);
	}



}
