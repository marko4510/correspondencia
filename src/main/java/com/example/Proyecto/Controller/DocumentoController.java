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
import javax.servlet.http.HttpSession;

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
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.TipoDocumentoService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;

@Controller
@RequestMapping("/documento")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    @Autowired
    private UnidadService unidadService;

     @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    Config config = new Config();

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            Usuario user = (Usuario) request.getSession().getAttribute("usuario");
            Usuario usuario = usuarioService.findById(user.getId_usuario());
            model.addAttribute("usuario", usuario);
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            Unidad unidad = user.getUnidad();

            List<MovimientoDocumento> movimientoDocumentosSolicitados = movimientoDocumentoService.ListaMovimientosSolicitados(unidad.getId_unidad().intValue());
        
            model.addAttribute("movimientoDocumentosSolicitados", movimientoDocumentosSolicitados);
            model.addAttribute("numSolicitud", movimientoDocumentosSolicitados.size());
            model.addAttribute("unidades", unidadService.findAll());

            model.addAttribute("opcion", "administrar cite");
            return "documento/ventana";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/tablaRegistros")
    public String tablaRegistros(Model model, HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        List<Documento> lDocumentos = documentoService
                .obtener_DocumentosUnidad(usuario.getUnidad().getId_unidad().intValue());

        String cite = "";
        for (Documento documento : lDocumentos) {
            Unidad unidad = unidadService.findById((long) documento.getUnidad_origen());
            if (documento.getCite() < 10) {
                cite = unidad.getSigla() + " N°" + "0" + documento.getCite();
            } else {
                cite = unidad.getSigla() + " N°" + documento.getCite();
            }
            documento.setCiteTexto(cite);
        }

        model.addAttribute("documentos", lDocumentos);
        return "documento/tablaRegistros";
    }

    @GetMapping("/formularioM")
    public String formulario(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            model.addAttribute("documento", new Documento());
            model.addAttribute("documentosUnidad", documentoService.obtener_DocumentosPorUnidadYGestion(user.getUnidad().getId_unidad().intValue(), gestion));
            model.addAttribute("tipoDocumentos", tipoDocumentoService.findAll());
            return "documento/formulario";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/formulario/{id_documento}")
    public String formulario(Model model, @PathVariable("id_documento") Long id, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            model.addAttribute("documento", documentoService.findById(id));
            model.addAttribute("tipoDocumentos", tipoDocumentoService.findAll());
            model.addAttribute("documentosUnidad", documentoService.obtener_DocumentosPorUnidadYGestion(user.getUnidad().getId_unidad().intValue(), gestion));
            model.addAttribute("edit", "true");
            return "documento/formulario";
        } else {
            return "redirect:/";
        }
    }

    // @PostMapping("/validarDocumento/{nroRuta}")
    // public ResponseEntity<String> formulario(@PathVariable("cite") String cite,
    // HttpServletRequest request) {
    // String currentYear = Year.now().toString();
    // Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
    // if (documentoService.obtener_DocumentosCiteGestionUnidad(cite,
    // usuario.getUnidad().getId_unidad().intValue(),
    // currentYear) != null) {
    // return ResponseEntity.ok("invalido");
    // }
    // return ResponseEntity.ok("valido");
    // }

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
            @RequestParam("file") MultipartFile archivo, HttpServletRequest request,
            @RequestParam(value = "numeroInicial", required = false) int numeroInicial) {
        try {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();

            // List<Documento> documentoActuales =
            // documentoService.obtener_DocumentosPorUnidadYGestionYTipoDocumento(unidad.getId_unidad().intValue(),
            // gestion, documento.getTipoDocumento().getId_tipo_documento());
            List<Documento> documentoActuales = documentoService
                    .obtener_DocumentosPorUnidadYGestion(unidad.getId_unidad().intValue(), gestion);
            if (documentoActuales.size() == 0) {
                System.out.println("Numero Inicial: " + numeroInicial);
                unidad.setContadorCite(numeroInicial);
                unidadService.save(unidad);
            } else {
                System.out.println("Numero Inicial: " + numeroInicial);
                int cont = unidad.getContadorCite() + 1;
                unidad.setContadorCite(cont);
                unidadService.save(unidad);
            }
            documento.setCite(unidad.getContadorCite());
            String arch = config.guardarArchivo((MultipartFile) archivo);
            documento.setRuta(arch);
            // documento.setNroRuta(cite);
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
            documento.setRef(doc.getRef());
            documento.setCite(doc.getCite());
            documento.setNroRuta(doc.getNroRuta());
            documentoService.save(documento);
            return ResponseEntity.ok("Modificado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

}
