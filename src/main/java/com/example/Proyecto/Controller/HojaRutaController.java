package com.example.Proyecto.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto.Config;
import com.example.Proyecto.Model.Documento;
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.EntidadExternaService;
import com.example.Proyecto.Service.HojaRutaService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.TipoDocumentoService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/hojaRuta")
public class HojaRutaController {

    @Autowired
    private HojaRutaService hojaRutaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    @Autowired
    private EntidadExternaService entidadExternaService;

    Config config = new Config();

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            return "hojaRuta/ventana";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/tablaRegistros")
    public String tablaRegistros(Model model, HttpServletRequest request) {
        
        Usuario user = (Usuario) request.getSession().getAttribute("usuario");
        Usuario usuario = usuarioService.findById(user.getId_usuario());
        String gestion = String.valueOf(LocalDate.now().getYear());
        List<HojaRuta> hojasRutasInterno = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestionYTipoDerivacion(usuario.getUnidad().getId_unidad().intValue(), gestion, "Interno");
        for (HojaRuta hojaRuta : hojasRutasInterno) {
            Usuario userEmi = usuarioService.findById(hojaRuta.getUsuario_emisor().longValue());
            hojaRuta.setHojaRutaTexto(usuario.getUnidad().getSigla()+"-"+hojaRuta.getNroRuta()+"/");
            hojaRuta.setNombreEmisorText(userEmi.getPersona().getNombre()+" "+userEmi.getPersona().getAp_materno()+" "+userEmi.getPersona().getAp_materno());
        }
        model.addAttribute("hojasRutasInterno", hojasRutasInterno);

        List<HojaRuta> hojasRutasExterno = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestionYTipoDerivacion(usuario.getUnidad().getId_unidad().intValue(), gestion, "Externo");
        for (HojaRuta hojaRuta : hojasRutasExterno) {
            hojaRuta.setHojaRutaTexto(usuario.getUnidad().getSigla()+"-"+hojaRuta.getNroRuta()+"/");
            hojaRuta.setNombreEmisorText(hojaRuta.getEntidadExterna().getNombre());
        }
        model.addAttribute("hojasRutasExterno", hojasRutasExterno);

        return "hojaRuta/tablaRegistros";
    }

    @GetMapping("/formulario")
    public String formulario(Model model, HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();
            model.addAttribute("hojaRuta", new HojaRuta());
            List<HojaRuta> hojasRutas = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestion(unidad.getId_unidad().intValue(), gestion);
            model.addAttribute("hojaRutasUnidad", hojasRutas);
            model.addAttribute("nroHojaRutaSiguiente", (unidad.getContadorHojaRuta()+1));
            model.addAttribute("unidades", unidadService.findAll());

            return "hojaRuta/formulario";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/formulario_externo")
    public String formulario_externo(Model model, HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();
            model.addAttribute("hojaRuta", new HojaRuta());
            List<HojaRuta> hojasRutas = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestion(unidad.getId_unidad().intValue(), gestion);
            model.addAttribute("hojaRutasUnidad", hojasRutas);
            model.addAttribute("nroHojaRutaSiguiente", (unidad.getContadorHojaRuta()+1));
            model.addAttribute("externos", entidadExternaService.findAll());
            model.addAttribute("unidades", unidadService.findAll());    
            
            return "hojaRuta/formulario_externo";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/persona_externa")
    public String persona_externa(Model model) {
        model.addAttribute("hojaRuta", new HojaRuta());
        model.addAttribute("externos", entidadExternaService.findAll());
        
        return "hojaRuta/persona_externa";
    }
    

    @PostMapping("/formulario/{id_hojaRuta}")
    public String formulario(Model model, @PathVariable("id_hojaRuta") Long id, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            HojaRuta hojaRuta = hojaRutaService.findById(id);
            Usuario userEmi = usuarioService.findById(hojaRuta.getUsuario_emisor().longValue());
            model.addAttribute("idResponsable", userEmi.getId_usuario());
            model.addAttribute("nombreResponsable", userEmi.getPersona().getNombre()+" "+userEmi.getPersona().getAp_materno()+" "+userEmi.getPersona().getAp_materno());
            model.addAttribute("idUnidad", userEmi.getUnidad().getId_unidad());
            model.addAttribute("hojaRuta", hojaRuta);
            List<HojaRuta> hojasRutas = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestion(user.getUnidad().getId_unidad().intValue(), gestion);
            model.addAttribute("hojaRutasUnidad", hojasRutas);
            
            model.addAttribute("unidades", unidadService.findAll());
            model.addAttribute("edit", "true");
            return "hojaRuta/formulario";
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

    @GetMapping("/verDocumento/{id_hojaRuta}")
    public ResponseEntity<Resource> verDocumento(@PathVariable("id_hojaRuta") Long id) throws IOException {
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

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@Validated HojaRuta hojaRuta,
            @RequestParam("file") MultipartFile archivo, HttpServletRequest request,
            @RequestParam("id_unidad_destino") Long id_unidad_destino,
            @RequestParam("observacion") String observacion,
            @RequestParam("instruccion") String instruccion,
            @RequestParam("userEmisor") Integer userEmisor,
            @RequestParam(value = "numeroInicial", required = false) int numeroInicial) {
        try {
            MovimientoDocumento movimientoDocumento = new MovimientoDocumento();
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();

            List<HojaRuta> hojasRutas = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestion(user.getUnidad().getId_unidad().intValue(), gestion);
            if (hojasRutas.size() == 0) {
                System.out.println("Numero Inicial: " + numeroInicial);
                unidad.setContadorHojaRuta(numeroInicial);
                unidadService.save(unidad);
            } else {
                System.out.println("Numero Inicial: " + numeroInicial);
                int cont = unidad.getContadorHojaRuta() + 1;
                unidad.setContadorHojaRuta(cont);
                unidadService.save(unidad);
            }

            hojaRuta.setNroRuta(unidad.getContadorHojaRuta());
            String arch = config.guardarArchivo((MultipartFile) archivo);
            hojaRuta.setRuta(arch);
            hojaRuta.setUsuario_emisor(userEmisor);
            hojaRuta.setUnidad_registro(unidad.getId_unidad().intValue());
            hojaRuta.setUsuario_registro(usuario.getId_usuario().intValue());
            // documento.setNroRuta(cite);
            hojaRuta.setFechaCreacion(new Date());
            hojaRuta.setEstado("A");
            hojaRuta.setTipo_derivacion("Interno");
            //hojaRuta.setUnidad_reg(usuario.getUnidad().getId_unidad().intValue());
            hojaRutaService.save(hojaRuta);


            Unidad unidadDestino = unidadService.findById(id_unidad_destino);
            movimientoDocumento.setRuta_movimiento(arch);
            movimientoDocumento.setHojaRuta(hojaRuta);
            movimientoDocumento.setFechaHoraRegistro(new Date());
            movimientoDocumento.setUnidadDestino(unidadDestino);
            movimientoDocumento.setUnidadOrigen(usuario.getUnidad());
            movimientoDocumento.setUsuarioRegistro(user.getId_usuario().intValue());
            movimientoDocumento.setInstruccion(instruccion);
            movimientoDocumento.setObservaciones(observacion);
            movimientoDocumentoService.save(movimientoDocumento);
            return ResponseEntity.ok("Registrado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    @PostMapping("/modificar")
    public ResponseEntity<String> modificar(@Validated HojaRuta hojaR, HttpServletRequest request,
            @RequestParam("userEmisor") Integer userEmisor,
            @RequestParam(value = "file", required = false) MultipartFile archivo) {
                HojaRuta hojaRuta = hojaRutaService.findById(hojaR.getId_hoja_ruta());
        try {
            if (archivo != null && !archivo.isEmpty()) {
                String arch = config.guardarArchivo(archivo);
                hojaRuta.setRuta(arch);
            }
            hojaRuta.setRef(hojaR.getRef());
            hojaRuta.setUsuario_emisor(userEmisor);
            hojaRutaService.save(hojaRuta);
            return ResponseEntity.ok("Modificado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    // ========= registrar hoja de ruta con entidad externa
    @PostMapping("/registrarExterno")
    public ResponseEntity<String> registrarExterna(@Validated HojaRuta hojaRuta,
            @RequestParam("file") MultipartFile archivo, HttpServletRequest request,
            @RequestParam("id_unidad_destino") Long id_unidad_destino,
            @RequestParam("observacion") String observacion,
            @RequestParam("instruccion") String instruccion,
            // @RequestParam("entidadEmisor") Long entidadEmisor,
            @RequestParam(value = "numeroInicial", required = false) int numeroInicial) {
        try {
            MovimientoDocumento movimientoDocumento = new MovimientoDocumento();
            String gestion = String.valueOf(LocalDate.now().getYear());
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();

            List<HojaRuta> hojasRutas = hojaRutaService.obtenerHojasDeRutaPorUnidadYGestion(user.getUnidad().getId_unidad().intValue(), gestion);
            if (hojasRutas.size() == 0) {
                System.out.println("Numero Inicial: " + numeroInicial);
                unidad.setContadorHojaRuta(numeroInicial);
                unidadService.save(unidad);
            } else {
                System.out.println("Numero Inicial: " + numeroInicial);
                int cont = unidad.getContadorHojaRuta() + 1;
                unidad.setContadorHojaRuta(cont);
                unidadService.save(unidad);
            }

            hojaRuta.setNroRuta(unidad.getContadorHojaRuta());
            String arch = config.guardarArchivo((MultipartFile) archivo);
            hojaRuta.setRuta(arch);
            //hojaRuta.setEntidadExterna(entidadExternaService.findById(entidadEmisor.longValue()));
            hojaRuta.setUnidad_registro(unidad.getId_unidad().intValue());
            hojaRuta.setUsuario_registro(usuario.getId_usuario().intValue());
            // documento.setNroRuta(cite);
            hojaRuta.setFechaCreacion(new Date());
            hojaRuta.setEstado("A");
            hojaRuta.setTipo_derivacion("Externo");
            //hojaRuta.setUnidad_reg(usuario.getUnidad().getId_unidad().intValue());
            hojaRutaService.save(hojaRuta);


            Unidad unidadDestino = unidadService.findById(id_unidad_destino);
            movimientoDocumento.setRuta_movimiento(arch);
            movimientoDocumento.setHojaRuta(hojaRuta);
            movimientoDocumento.setFechaHoraRegistro(new Date());
            movimientoDocumento.setUnidadDestino(unidadDestino);
            movimientoDocumento.setUnidadOrigen(usuario.getUnidad());
            movimientoDocumento.setUsuarioRegistro(user.getId_usuario().intValue());
            movimientoDocumento.setInstruccion(instruccion);
            movimientoDocumento.setObservaciones(observacion);
            movimientoDocumentoService.save(movimientoDocumento);
            return ResponseEntity.ok("Registrado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }
}
