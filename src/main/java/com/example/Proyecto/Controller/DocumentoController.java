package com.example.Proyecto.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.mail.Multipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public String formulario(Model model, @PathVariable("id") Long id) {
        model.addAttribute("documento", documentoService.findById(id));
        return "documento/formulario";
    }

    @GetMapping("/verDocumento/{id}")
    public ResponseEntity<Resource> verDocumento(@PathVariable Long id) throws IOException {
        Documento documento = documentoService.findById(id);

        // Obtener la ruta completa del archivo
        Path projectPath = Paths.get("").toAbsolutePath();
        String ruta = projectPath + "/uploads/" + documento.getRuta();

        // Cargar el archivo PDF como recurso
        Resource resource = new InputStreamResource(new FileInputStream(ruta));

        // Configurar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + documento.getCite()); // "inline"
                                                                                                                // para
                                                                                                                // visualizar
                                                                                                                // en el
                                                                                                                // navegador
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Devolver la respuesta con el archivo PDF
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@Validated Documento documento,
            @RequestParam("file") MultipartFile archivo) {
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

    @PostMapping("/modificar")
    public ResponseEntity<String> modificar(@Validated Documento doc,
            @RequestParam(value = "file", required = false) MultipartFile archivo) {
        Documento documento = documentoService.findById(doc.getId_documento());
        try {
            if (archivo != null) {
                String arch = config.guardarArchivo((MultipartFile) archivo);
                documento.setRuta(arch);
            } else {
                documento.setRuta(doc.getRuta());
            }

            documento.setFechaCreacion(new Date());
            documento.setEstado("A");
            documentoService.save(documento);
            return ResponseEntity.ok("Modificado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

}
