package com.example.Proyecto.Controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.UnidadService;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping(value = "/seguimiento")
public class SeguimientoController {
    
    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UnidadService unidadService;

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("usuario") != null) {
            List<Documento> documentos = documentoService.findAll();

            // Extraer los años y almacenarlos en un Set para evitar duplicados
            Set<Integer> years = documentos.stream()
                    .map(doc -> doc.getFechaCreacion().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().getYear())
                    .collect(Collectors.toSet());

            // Añadir los documentos y los años al modelo
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
                return "seguimiento/flujoDocumento"; // Retorna la vista si hay resultados
            } else {
                return "seguimiento/noEncontrado"; // Retorna una vista alternativa si no hay resultados
            }
        }

        @GetMapping("/verDocumentoMovimiento/{id}")
        public ResponseEntity<Resource> verDocumentoMovimiento(@PathVariable("id") Long id) throws IOException {
            MovimientoDocumento movimientoDocumento = movimientoDocumentoService.findById(id);
            // Obtener la ruta completa del archivo
            Path projectPath = Paths.get("").toAbsolutePath();
            String ruta = projectPath + "/uploads/" + movimientoDocumento.getRuta_movimiento();
            System.out.println(ruta);
            // Cargar el archivo PDF como recurso
            Resource resource = new InputStreamResource(new FileInputStream(ruta));

            // Configurar las cabeceras de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=" + movimientoDocumento.getDocumento().getCite());
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Devolver la respuesta con el archivo PDF
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }
    
}
