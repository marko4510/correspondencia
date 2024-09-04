package com.example.Proyecto.Controller;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import javax.persistence.EntityNotFoundException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

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
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.HojaRutaService;
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


    @Autowired
    private HojaRutaService hojaRutaService;

    Config config = new Config();

    @RequestMapping(value = "/recepcion", method = RequestMethod.GET)
    public String prueba(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            List<HojaRuta> hojaRutas = hojaRutaService.findAll();
            // Extraer los años y almacenarlos en un Set para evitar duplicados
            Set<Integer> years = hojaRutas.stream()
                    .map(hr -> hr.getFechaCreacion().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().getYear())
                    .collect(Collectors.toSet());

        

            // Añadir los documentos y los años al modelo
            // model.addAttribute("unidades",
            // unidadService.findUnidadesNoRelacionadasConUsuario(usuario.getId_usuario()));
            model.addAttribute("unidades", unidadService.findAll());
            model.addAttribute("years", years);
            model.addAttribute("opcion", "administrar recepcion");
            return "busqueda/recepcion";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/formularioDocumento")
    public ResponseEntity<?> formularioDocumento(@RequestParam(name = "nro_ruta") Integer nro_ruta,
            @RequestParam(name = "year") String year, @RequestParam(name = "id_unidad") Integer id_unidad, Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        HojaRuta hojaRuta = hojaRutaService.obtenerHojaRutaPorGestionUnidad(nro_ruta, id_unidad, year);
      
        

        if (hojaRuta == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No se encontró documento con el número de Hoja de Ruta proporcionado.");
        }
        
        //Unidad unidad = unidadService.findById((long) hojaRuta.getUnidad_reg());
        // if (hojaRuta.getNroRuta() < 10) {
        //     hojaRuta.setCiteTexto(unidad.getSigla() + " N°" + "0" + documento.getCite());
        // } else {
        //     documento.setCiteTexto(unidad.getSigla() + " N°" + documento.getCite());
        // }

        // List<HojaRuta> hojaRutaActuales = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestion(unidad.getId_unidad().intValue(), year);
        // if (hojaRutaActuales.size() == 0) {
        //     model.addAttribute("hojasRutaUnidad", hojaRutaActuales.size());
        // } else {
        //     model.addAttribute("hojasRutaUnidad", unidad.getContadorHojaRuta());
        // }
        
        model.addAttribute("hojaRuta", hojaRuta);
        model.addAttribute("unidades", unidadService.findAll());
    


        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariables(model.asMap());

        String htmlContent = templateEngine.process("busqueda/formulario", context);
        return ResponseEntity.ok().body(htmlContent);
    }

    @PostMapping("/regMovimientoDocumento")
    public ResponseEntity<String> regMovimientoDocumento(
            @RequestParam("id_hoja_ruta") Long id_hoja_ruta,
            @RequestParam("id_unidad_destino") Long id_unidad_destino,
            @RequestParam("userDestino") Integer id_user_destino,
            @RequestParam("observacion") String observacion,
            @RequestParam("instruccion") String instruccion,
            @RequestParam("file") MultipartFile archivo, Model model, HttpServletRequest request
            ) {
        try {
            MovimientoDocumento movimientoDocumento = new MovimientoDocumento();
            HojaRuta hojaRuta = hojaRutaService.findById(id_hoja_ruta);
            if (archivo != null && !archivo.isEmpty()) {
                String arch = config.guardarArchivo(archivo);
                movimientoDocumento.setRuta_movimiento(arch);
            }
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            long idUsuarioLong = usuario.getId_usuario();
            Integer idUsuarioInt = (int) idUsuarioLong;
            Unidad unidad = usuario.getUnidad();
            long idUnidad = usuario.getUnidad().getId_unidad();
            Integer idUnidadInt = (int) idUnidad;
            Unidad unidadDestino = unidadService.findById(id_unidad_destino);
           
            
            movimientoDocumento.setHojaRuta(hojaRuta);
            movimientoDocumento.setEstado_movimiento("P");
            movimientoDocumento.setFechaHoraRegistro(new Date());
            movimientoDocumento.setUnidadDestino(unidadDestino);
            movimientoDocumento.setUnidadOrigen(usuario.getUnidad());
            movimientoDocumento.setUsuarioRegistro(idUsuarioInt);
            movimientoDocumento.setInstruccion(instruccion);
            movimientoDocumento.setObservaciones(observacion);
            movimientoDocumento.setUsuarioDestino(id_user_destino);
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
            List<HojaRuta> hojaRutas = hojaRutaService.findAll();
            // Extraer los años y almacenarlos en un Set para evitar duplicados
            Set<Integer> years = hojaRutas.stream()
                    .map(hr -> hr.getFechaCreacion().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().getYear())
                    .collect(Collectors.toSet());
            // Añadir los documentos y los años al modelo
            model.addAttribute("unidades", unidadService.findAll());
            model.addAttribute("years", years);
            return "busqueda/formularioRecepcion";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/cargarSelectUsuarios/{idUnidad}")
    public ResponseEntity<List<String[]>> cargarSelectUsuarios(@PathVariable("idUnidad") Long id,
            HttpServletRequest request) {
        Usuario userLog = (Usuario) request.getSession().getAttribute("usuario");
        List<String[]> listaUsuarios = new ArrayList<>();
        List<Usuario> listaUser = usuarioService.listarUsuariosPorUnidad(id, userLog.getId_usuario());
        for (Usuario usuario : listaUser) {
            String[] user = { usuario.getId_usuario().toString(), usuario.getPersona().getNombre() + " "
                    + usuario.getPersona().getAp_paterno() + " " + usuario.getPersona().getAp_materno() };
          
            listaUsuarios.add(user);
        }

        return ResponseEntity.ok(listaUsuarios);
    }

    @GetMapping("/verDocumentoHojaRuta/{id_hoja_ruta}")
    public ResponseEntity<Resource> verDocumentoHojaRuta(@PathVariable("id_hoja_ruta") Long id) throws IOException {
        HojaRuta hojaRuta = hojaRutaService.findById(id);


        // Obtener la ruta completa del archivo
        Path projectPath = Paths.get("").toAbsolutePath();
        String ruta = projectPath + "/uploads/" + hojaRuta.getRuta();
        System.out.println(ruta);
        // Cargar el archivo PDF como recurso
        Resource resource = new InputStreamResource(new FileInputStream(ruta));

        // Configurar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + hojaRuta.getNroRuta()); // "inline"
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


    @PostMapping("/aceptar-recepcion")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> aceptarRecepcion(@RequestBody Map<String, Long> payload) {
        Long id = payload.get("id");
        Map<String, Boolean> response = new HashMap<>();
        
        try {
            MovimientoDocumento movimiento = movimientoDocumentoService.findById(id);
            if (movimiento == null) {
                throw new EntityNotFoundException("Movimiento no encontrado con id: " + id);
            }
            
            movimiento.setEstado_movimiento("A");
            movimientoDocumentoService.save(movimiento);
            
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("success", false);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
