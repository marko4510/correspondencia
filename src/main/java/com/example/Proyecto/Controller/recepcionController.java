package com.example.Proyecto.Controller;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.example.Proyecto.Config;
import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class recepcionController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private UsuarioService usuarioService;

    Config config = new Config();

    @RequestMapping(value = "/recepcion", method = RequestMethod.GET)
    public String prueba(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            List<Documento> documentos = documentoService.findAll();
            // Extraer los años y almacenarlos en un Set para evitar duplicados
            Set<Integer> years = documentos.stream()
                    .map(doc -> doc.getFechaCreacion().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().getYear())
                    .collect(Collectors.toSet());

            // Añadir los documentos y los años al modelo
            //model.addAttribute("unidades", unidadService.findUnidadesNoRelacionadasConUsuario(usuario.getId_usuario()));
            model.addAttribute("unidades", unidadService.findAll());
            model.addAttribute("years", years);
            return "busqueda/recepcion";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/formularioDocumento")
    public ResponseEntity<?> formularioDocumento(@RequestParam(name = "nroRuta") String nroRuta,
            @RequestParam(name = "year") String year, @RequestParam(name = "id_unidad") Integer id_unidad, Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        Documento documento = documentoService.obtener_DocumentosRutaGestionUnidad(nroRuta, id_unidad, year);

        if (documento == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No se encontró documento con el número de Hoja de Ruta proporcionado.");
        }

        model.addAttribute("documento", documento);
        model.addAttribute("unidades", unidadService.findUnidadesNoRelacionadasConUsuario(usuario.getId_usuario()));

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariables(model.asMap());

        String htmlContent = templateEngine.process("busqueda/formulario", context);
        return ResponseEntity.ok().body(htmlContent);
    }

    @PostMapping("/regMovimientoDocumento")
    public ResponseEntity<String> regMovimientoDocumento(
            @RequestParam("id_documento") Long id_documento,
            @RequestParam("id_unidad_destino") Long id_unidad_destino,
            @RequestParam("observacion") String observacion,
            @RequestParam("file") MultipartFile archivo, Model model, HttpServletRequest request) {
        try {
            MovimientoDocumento movimientoDocumento = new MovimientoDocumento();
            Documento documento = documentoService.findById(id_documento);
            if (archivo != null && !archivo.isEmpty()) {
                String arch = config.guardarArchivo(archivo);
                movimientoDocumento.setRuta_movimiento(arch);
            } 

            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            long idUsuarioLong = usuario.getId_usuario();
            Integer idUsuarioInt = (int) idUsuarioLong;

            
            Unidad unidadDestino = unidadService.findById(id_unidad_destino);

            
            movimientoDocumento.setDocumento(documento);
            movimientoDocumento.setFechaHoraRegistro(new Date());
            movimientoDocumento.setUnidadDestino(unidadDestino);
            movimientoDocumento.setUnidadOrigen(usuario.getUnidad());
            movimientoDocumento.setUsuarioRegistro(idUsuarioInt);
            
            movimientoDocumento.setObservaciones(observacion);
            movimientoDocumentoService.save(movimientoDocumento);

            return ResponseEntity.ok("Registrado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    @PostMapping("/cargarFormularioBusquedaExterna")
    public String postMethodName(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            List<Documento> documentos = documentoService.findAll();
            // Extraer los años y almacenarlos en un Set para evitar duplicados
            Set<Integer> years = documentos.stream()
                    .map(doc -> doc.getFechaCreacion().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().getYear())
                    .collect(Collectors.toSet());

            // Añadir los documentos y los años al modelo
            model.addAttribute("unidades", unidadService.findUnidadesNoRelacionadasConUsuario(usuario.getId_usuario()));
            model.addAttribute("years", years);
            return "busqueda/formularioRecepcion";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/cargarSelectUsuarios/{idUnidad}")
    public ResponseEntity<List<String []>> cargarSelectUsuarios(@PathVariable("idUnidad") Long id, HttpServletRequest request) {
        Usuario userLog = (Usuario) request.getSession().getAttribute("usuario");
        List<String[]> listaUsuarios = new ArrayList<>();
        List<Usuario> listaUser = usuarioService.listarUsuariosPorUnidad(id, userLog.getId_usuario());
        for (Usuario usuario : listaUser) {
            String[] user = {usuario.getId_usuario().toString(), usuario.getPersona().getNombre()+" "+usuario.getPersona().getAp_paterno()+" "+usuario.getPersona().getAp_materno()};
            listaUsuarios.add(user);
        }
        
        return ResponseEntity.ok(listaUsuarios);
    }

        
}
