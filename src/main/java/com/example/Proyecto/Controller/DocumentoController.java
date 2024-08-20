package com.example.Proyecto.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;

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
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.UsuarioService;

@Controller
@RequestMapping("/documento")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UsuarioService usuarioService;

    Config config = new Config();

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            return "documento/ventana";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/tablaRegistros")
    public String tablaRegistros(Model model, HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

        model.addAttribute("documentos",
                documentoService.obtener_DocumentosUnidad(usuario.getUnidad().getId_unidad().intValue()));
        return "documento/tablaRegistros";
    }

    @GetMapping("/formularioM")
    public String formulario(Model model) {
        model.addAttribute("documento", new Documento());
        return "documento/formulario";
    }

    @PostMapping("/formulario/{id_documento}")
    public String formulario(Model model, @PathVariable("id_documento") Long id) {
        model.addAttribute("documento", documentoService.findById(id));
        model.addAttribute("edit", "true");
        return "documento/formulario";
    }

    @PostMapping("/validarDocumento/{nroRuta}")
    public ResponseEntity<String> formulario(@PathVariable("nroRuta") String nroRuta, HttpServletRequest request) {
        String currentYear = Year.now().toString();
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (documentoService.obtener_DocumentosRutaGestionUnidad(nroRuta, usuario.getUnidad().getId_unidad().intValue(),
                currentYear) != null) {
            return ResponseEntity.ok("invalido");
        }
        return ResponseEntity.ok("valido");
    }

    @GetMapping("/verDocumento/{id_documento}")
    public ResponseEntity<Resource> verDocumento(@PathVariable("id_documento") Long id) throws IOException {
        Documento documento = documentoService.findById(id);

        // Obtener la ruta completa del archivo
        Path projectPath = Paths.get("").toAbsolutePath();
        String ruta = projectPath + "/uploads/" + documento.getRuta();
        System.out.println(ruta);
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
            @RequestParam("file") MultipartFile archivo, HttpServletRequest request) {
        try {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();
            
            List<Documento> documentoActuales = documentoService.obtener_DocumentosPorUnidadYGestion(unidad.getId_unidad().intValue(), gestion);


            String arch = config.guardarArchivo((MultipartFile) archivo);
            documento.setRuta(arch);

            String cite = unidad.getSigla()+" N°"+documentoActuales.size()+"/"+gestion;
            documento.setCite(cite);
            //documento.setNroRuta(cite);
            documento.setFechaCreacion(new Date());
            documento.setEstado("A");
            documento.setUnidad_origen(usuario.getUnidad().getId_unidad().intValue());
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
            if (archivo != null && !archivo.isEmpty()) {
                String arch = config.guardarArchivo(archivo);
                documento.setRuta(arch);
            }
            documento.setAsunto(doc.getAsunto());
            documento.setCite(doc.getCite());
            documento.setNroRuta(doc.getNroRuta());
            documentoService.save(documento);
            return ResponseEntity.ok("Modificado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

}
