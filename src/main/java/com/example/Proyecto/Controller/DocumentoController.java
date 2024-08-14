package com.example.Proyecto.Controller;

import java.util.Date;

import javax.mail.Multipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto.Config;
import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Service.DocumentoService;


@Controller
@RequestMapping("/documento")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    Config config = new Config();

    @GetMapping("/inicio")
    public String inicio() {
        return "documento/ventana";
    }

    @PostMapping("/tablaRegistros")
    public String tablaRegistros(Model model) {
        model.addAttribute("documentos", documentoService.findAll());
        return "documento/tablaRegistros";
    }

    @PostMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("documento", new Documento());
        return "documento/formulario";
    }

    @PostMapping("/formulario/{id}")
    public String formulario(Model model, @PathVariable("id")Long id) {
        model.addAttribute("documento", documentoService.findById(id));
        return "documento/formulario";
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@Validated Documento documento, @RequestParam("file")MultipartFile archivo) {
        try {
            String arch = config.guardarArchivo((MultipartFile) archivo);
            documento.setRuta(arch);
            documento.setFechaCreacion(new Date());
            documento.setEstado("A");
            documentoService.save(documento);
            return ResponseEntity.ok("Registrado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }
    
}
