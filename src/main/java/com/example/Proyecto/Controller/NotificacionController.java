package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
import com.example.Proyecto.Service.TipoDocumentoService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class NotificacionController {

    @Autowired
    private TipoDocumentoService tipoDocumentoService;
    
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


    @PostMapping("/regMovimientoHojaRutaNotificacion")
    public ResponseEntity<String> regMovimientoHojaRutaNotificacion(
            @RequestParam(name = "id_hoja_ruta", required = false) Long id_hoja_ruta,
            @RequestParam(name = "id_movimiento_documento", required = false) Long id_movimiento,
            @RequestParam(name = "id_unidad_destino", required = false) Long id_unidad_destino,
            @RequestParam(name = "userDestino", required = false) Integer id_user_destino,
            @RequestParam(name = "observacion", required = false) String observacion,
            @RequestParam("instruccion") String instruccion,
            @RequestParam(name = "file", required = false) MultipartFile archivo,
            HttpServletRequest request) {
        try {
            // Log de los parámetros recibidos
            System.out.println("Parámetros recibidos:");
            System.out.println("id_hoja_ruta: " + id_hoja_ruta);
            System.out.println("id_movimiento_documento: " + id_movimiento);
            System.out.println("id_unidad_destino: " + id_unidad_destino);
            System.out.println("id_user_destino: " + id_user_destino);
            System.out.println("observacion: " + observacion);
            System.out.println("instruccion: " + instruccion);
            System.out.println("archivo: " + (archivo != null ? archivo.getOriginalFilename() : "null"));

            // Validaciones iniciales
            if (id_hoja_ruta == null) {
                return ResponseEntity.badRequest().body("El ID de hoja de ruta es requerido y no puede ser null");
            }
            if (id_movimiento == null) {
                return ResponseEntity.badRequest()
                        .body("El ID de movimiento documento es requerido y no puede ser null");
            }
            if (id_unidad_destino == null && !instruccion.equals("Archivar")) {
                return ResponseEntity.badRequest().body("Debe seleccionar una unidad de destino y no puede ser null");
            }
            if (id_user_destino == null && !instruccion.equals("Archivar")) {
                return ResponseEntity.badRequest().body("Debe seleccionar un usuario de destino y no puede ser null");
            }

            // Buscar y validar el movimiento de documento actual
            MovimientoDocumento movimientoDocumentoActual = movimientoDocumentoService.findById(id_movimiento);
            if (movimientoDocumentoActual == null) {
                return ResponseEntity.badRequest().body("No se encontró el movimiento documento especificado");
            }

            // Actualizar el estado del movimiento documento actual
            movimientoDocumentoActual.setEstado_movimiento("A");
            movimientoDocumentoService.save(movimientoDocumentoActual);

            // Buscar y validar la hoja de ruta
            HojaRuta hojaRuta = hojaRutaService.findById(id_hoja_ruta);
            if (hojaRuta == null) {
                return ResponseEntity.badRequest().body("No se encontró la hoja de ruta especificada");
            }

            // Buscar y validar la unidad de destino, excepto cuando la instrucción es
            // "Archivar"
            Unidad unidadDestino = null;
            if (!instruccion.equals("Archivar")) {
                unidadDestino = unidadService.findById(id_unidad_destino);
                if (unidadDestino == null) {
                    return ResponseEntity.badRequest().body("La unidad de destino seleccionada no es válida");
                }
            }

            // Obtener información del usuario actual
            Usuario usuarioActual = (Usuario) request.getSession().getAttribute("usuario");
            if (usuarioActual == null) {
                return ResponseEntity.badRequest().body("No se pudo obtener la información del usuario actual");
            }

            // Crear nuevo movimiento de documento
            MovimientoDocumento nuevoMovimientoDocumento = new MovimientoDocumento();

            // Procesar el archivo adjunto si existe
            if (archivo != null && !archivo.isEmpty()) {
                try {
                    String rutaArchivo = config.guardarArchivo(archivo);
                    nuevoMovimientoDocumento.setRuta_movimiento(rutaArchivo);
                } catch (Exception e) {
                    System.out.println("Error al guardar el archivo: " + e.getMessage());
                    return ResponseEntity.badRequest().body("Error al procesar el archivo adjunto");
                }
            }

            // Configurar el nuevo movimiento de documento
            nuevoMovimientoDocumento.setHojaRuta(hojaRuta);
            if (instruccion.equals("Archivar")) {
                nuevoMovimientoDocumento.setEstado_movimiento("HA"); // Estado de archivado
            } else {
                nuevoMovimientoDocumento.setEstado_movimiento("P"); // Estado pendiente
                nuevoMovimientoDocumento.setUnidadDestino(unidadDestino);
            }
            nuevoMovimientoDocumento.setFechaHoraRegistro(new Date());
            nuevoMovimientoDocumento.setUnidadOrigen(usuarioActual.getUnidad());
            nuevoMovimientoDocumento.setUsuarioRegistro(usuarioActual.getId_usuario().intValue());
            nuevoMovimientoDocumento.setInstruccion(instruccion);
            nuevoMovimientoDocumento.setObservaciones(observacion);
            nuevoMovimientoDocumento.setUsuarioDestino(id_user_destino);

            // Guardar el nuevo movimiento de documento
            try {
                movimientoDocumentoService.save(nuevoMovimientoDocumento);
                return ResponseEntity.ok("Movimiento de documento registrado exitosamente");
            } catch (Exception e) {
                System.out.println("Error al guardar el nuevo movimiento de documento: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al guardar el nuevo movimiento de documento");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }


    @PostMapping("/solitudes_Pendientes")
    public String solitudes_Pendientes(Model model, HttpServletRequest request) {
        try {
            Usuario user = (Usuario) request.getSession().getAttribute("usuario");
            if (user == null) {
                throw new IllegalStateException("Usuario no encontrado en la sesión.");
            }

            Usuario usuario = usuarioService.findById(user.getId_usuario());
            model.addAttribute("usuario", usuario);

            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);

            Unidad unidad = user.getUnidad();
            if (unidad == null) {
                throw new IllegalStateException("Unidad no encontrada para el usuario.");
            }

            List<MovimientoDocumento> movimientoDocumentosSolicitados = movimientoDocumentoService
                    .ListaMovimientosSolicitados(unidad.getId_unidad().intValue());

            model.addAttribute("movimientoDocumentosSolicitados", movimientoDocumentosSolicitados);

            return "notificacion/Modal_Solitidudes_Pendientes";
        } catch (Exception e) {
            e.printStackTrace(); // Puedes cambiar esto por un log adecuado en producción
            model.addAttribute("error", "Ocurrió un error al procesar la solicitud.");
            return "error"; // O un template de error adecuado
        }
    }

    @PostMapping("/formulario_Solicitud/{id_movimiento_documento}")
    public String formulario_Solicitud(Model model, @PathVariable(name = "id_movimiento_documento",required = false)Long id_movimiento_documento) {
        MovimientoDocumento movimientoDocumento = movimientoDocumentoService.findById(id_movimiento_documento);
        model.addAttribute("md", movimientoDocumento);
        model.addAttribute("hojaRuta", movimientoDocumento.getHojaRuta());
        model.addAttribute("unidades", unidadService.findAll());

        return "notificacion/modal-solicitudes-form";
    }
    
    @PostMapping("/cites_generados")
    public String cites_generados(Model model) {

        model.addAttribute("TipoDocumentos", tipoDocumentoService.findAll());
        
        return "notificacion/Modal_Cites_Generados";
    }
    
    @PostMapping("/lista_cites/{id_tipo_documento}")
    public String lista_cites(Model model,@PathVariable(name = "id_tipo_documento",required = false)Long id_tipo_documento, HttpServletRequest request) {

        Usuario user = (Usuario) request.getSession().getAttribute("usuario");
        String gestion = String.valueOf(LocalDate.now().getYear());
        Unidad unidad1 = user.getUnidad();

        List<Documento> lDocumentos = documentoService.obtener_DocumentosPorUnidadYGestionYTipoDocumento(unidad1.getId_unidad().intValue(),gestion,id_tipo_documento);

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

        model.addAttribute("cites", lDocumentos);
    
        return "notificacion/Lista_Cites_Generados";
    }
    
    @PostMapping("/archivados")
    public String archivados(Model model, HttpServletRequest request) {
        try {
            Usuario user = (Usuario) request.getSession().getAttribute("usuario");
            if (user == null) {
                throw new IllegalStateException("Usuario no encontrado en la sesión.");
            }

            String gestion = String.valueOf(LocalDate.now().getYear());

            Unidad unidad = user.getUnidad();
            if (unidad == null) {
                throw new IllegalStateException("Unidad no encontrada para el usuario.");
            }

            List<MovimientoDocumento> movimientoDocumentosSolicitados = movimientoDocumentoService
                    .Lista_Archivados(unidad.getId_unidad(),gestion);
            List<MovimientoDocumento> lmovimientoDocumentosSolicitados = movimientoDocumentoService
                    .Lista_Archivados(unidad.getId_unidad(),gestion);

            // model.addAttribute("Movimientosarchivados", movimientoDocumentosSolicitados);

            String cite = "";
            String unidadOrigenText = "";
            for (MovimientoDocumento movimientoDocumento : lmovimientoDocumentosSolicitados) {
                Unidad unidadOrigen = unidadService.findById(movimientoDocumento.getHojaRuta().getUnidad_registro().longValue());
                
                    cite = unidadOrigen.getSigla() + "-" + movimientoDocumento.getHojaRuta().getNroRuta() + "/"+ gestion;
                    unidadOrigenText = unidadOrigen.getNombre();
                    movimientoDocumento.setUnidadOrigenTexto(unidadOrigenText);
                    movimientoDocumento.setCiteTexto(cite);
            }

            model.addAttribute("Movimientosarchivados", movimientoDocumentosSolicitados);


            return "notificacion/Modal_Archivados";
        } catch (Exception e) {
            e.printStackTrace(); // Puedes cambiar esto por un log adecuado en producción
            model.addAttribute("error", "Ocurrió un error al procesar la solicitud.");
            return "error"; // O un template de error adecuado
        }
    }
}
