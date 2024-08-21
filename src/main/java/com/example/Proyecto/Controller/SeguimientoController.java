package com.example.Proyecto.Controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.UnidadService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;



@Controller
@RequestMapping(value = "/seguimiento")
public class SeguimientoController {
    
    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("usuario") != null) {
            List<Documento> documentos = documentoService.findAll();

            Set<Integer> years = documentos.stream()
                    .map(doc -> doc.getFechaCreacion()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .getYear())
                    .collect(Collectors.toSet());

            // Verifica el contenido de years
    
            model.addAttribute("unidades", unidadService.findAll());
            model.addAttribute("years", years);
           
            return "seguimiento/ventana";
        } else {
            return "redirect:/login";
        }
    }
    

   @PostMapping("/buscar_documento/{nroRuta}/{year}/{id_unidad}")
        public String buscar_documento(@PathVariable(name = "nroRuta") String nroRuta,
        @PathVariable(name = "year")String year,
        @PathVariable(name = "id_unidad")Integer id_unidad, Model model) {

            // List<MovimientoDocumento> flujoDocumentos = movimientoDocumentoService.obtener_Flujo_Documento(nroRuta);
            List<MovimientoDocumento> flujoDocumentos = movimientoDocumentoService.obtener_Flujos_Documentos(nroRuta,id_unidad,year);

            if (flujoDocumentos.size() > 0) {
                model.addAttribute("flujo", flujoDocumentos);
                model.addAttribute("documento", flujoDocumentos.get(0).getDocumento());
                Integer id = flujoDocumentos.get(0).getDocumento().getUnidad_origen();
                Unidad unidad = unidadService.findById(id.longValue());
                model.addAttribute("unidad", unidad);
                return "seguimiento/flujoDocumento"; // Retorna la vista si hay resultados
            } else {
                return "seguimiento/noEncontrado"; // Retorna una vista alternativa si no hay resultados
            }
        }

        @GetMapping("/verDocumentoMovimiento/{id}")
        public ResponseEntity<Resource> verDocumentoMovimiento(@PathVariable("id") Long id) {
            MovimientoDocumento movimientoDocumento = movimientoDocumentoService.findById(id);

            // Verifica si el movimientoDocumento no es nulo y si la ruta del movimiento no
            // está vacía o es nula
            if (movimientoDocumento != null && movimientoDocumento.getRuta_movimiento() != null
                    && !movimientoDocumento.getRuta_movimiento().trim().isEmpty()) {
                // Obtener la ruta completa del archivo
                Path projectPath = Paths.get("").toAbsolutePath();
                String ruta = projectPath.resolve("uploads").resolve(movimientoDocumento.getRuta_movimiento())
                        .toString();
                System.out.println("Ruta del archivo: " + ruta);

                try {
                    // Verificar si el archivo realmente existe en el sistema de archivos
                    File file = new File(ruta);
                    if (file.exists()) {
                        // Cargar el archivo PDF como recurso
                        Resource resource = new InputStreamResource(new FileInputStream(file));

                        // Configurar las cabeceras de la respuesta
                        HttpHeaders headers = new HttpHeaders();
                        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=" + movimientoDocumento.getDocumento().getCite());
                        headers.setContentType(MediaType.APPLICATION_PDF);

                        // Devolver la respuesta con el archivo PDF
                        return ResponseEntity.ok()
                                .headers(headers)
                                .body(resource);
                    } else {
                        // Archivo no encontrado en la ruta especificada
                        return ResponseEntity.notFound().build();
                    }
                } catch (IOException e) {
                    // Manejar excepciones de IO
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                // Devolver una respuesta 404 Not Found si el documento no existe o la ruta está
                // vacía
                return ResponseEntity.notFound().build();
            }
        }
    
        @GetMapping("/hojaruta/{id_documento}")
        public ResponseEntity<?> generarHojaRuta(@PathVariable(name = "id_documento") Long id_documento, Model model,
                HttpServletRequest request,
                HttpServletResponse response) {

            Documento documento = documentoService.findById(id_documento);

            model.addAttribute("documento", documentoService.findById(id_documento));        
            model.addAttribute("hojaRuta", documentoService.findById(id_documento).getHojaRuta());
            model.addAttribute("unidad", unidadService.findById(documento.getUnidad_origen().longValue()));
            WebContext context = new WebContext(request, response, request.getServletContext());
            context.setVariables(model.asMap());

            String htmlContent = templateEngine.process("seguimiento/hojaDeRuta", context);

            return ResponseEntity.ok().body(htmlContent);
        }
        
        
}
