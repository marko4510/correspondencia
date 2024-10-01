package com.example.Proyecto.Controller;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.HojaRutaService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;

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

    @Autowired
    private HojaRutaService hojaRutaService;

    @Autowired
    private UsuarioService usuarioService;

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
            
            List<HojaRuta> hojaRutas = hojaRutaService.findAll();

            Set<Integer> years = hojaRutas.stream()
                    .map(doc -> doc.getFechaCreacion()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .getYear())
                    .collect(Collectors.toSet());

            // Verifica el contenido de years

            model.addAttribute("unidades", unidadService.findAll());
            model.addAttribute("years", years);
            model.addAttribute("opcion", "administrar seguimiento");
            return "seguimiento/ventana";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/buscar_documento/{nroRuta}/{year}/{id_unidad}")
    public String buscar_documento(@PathVariable(name = "nroRuta") String nroRuta,
            @PathVariable(name = "year") String year,
            @PathVariable(name = "id_unidad") Integer id_unidad, Model model) {

        // List<MovimientoDocumento> flujoDocumentos =
        // movimientoDocumentoService.obtener_Flujo_Documento(nroRuta);
        Integer num_ruta = Integer.parseInt(nroRuta);
        List<MovimientoDocumento> flujoDocumentos = movimientoDocumentoService.obtener_Flujos_Documentos(num_ruta,
                id_unidad, year);

        if (flujoDocumentos.size() > 0) {
            HojaRuta hojaRuta = flujoDocumentos.get(0).getHojaRuta();
            model.addAttribute("flujo", flujoDocumentos);
            model.addAttribute("hojaDeRuta", hojaRuta);

            if (hojaRuta.getTipo_derivacion().equals("Interno")) {
                Integer id_usuario_emisor = flujoDocumentos.get(0).getHojaRuta().getUsuario_emisor();
                Long id_usuario_emisor_long = id_usuario_emisor.longValue();
                Usuario usuario_emisor = usuarioService.findById(id_usuario_emisor_long);
                model.addAttribute("usuario_emisor", usuario_emisor);
                model.addAttribute("interno", true);
            }
            if (hojaRuta.getTipo_derivacion().equals("Externo")) {
                model.addAttribute("usuario_emisor", hojaRuta.getEntidadExterna().getNombre());
                model.addAttribute("externo", true);
            }

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
                            "inline; filename=" + movimientoDocumento.getHojaRuta().getId_hoja_ruta());
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

  

    public static String generarAlfanumerico(Long valor) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder resultado = new StringBuilder();
        Random random = new Random(valor); // Usamos el Long como semilla para el generador de números aleatorios

        // Determinamos la longitud de la cadena basándonos en el valor de Long (por
        // ejemplo, 8 caracteres)
        int longitud = 8;

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            resultado.append(caracteres.charAt(index));
        }

        return resultado.toString();
    }

    @GetMapping("/hojaruta/{id_hoja_ruta}")
    public ResponseEntity<?> generarHojaRuta(@PathVariable(name = "id_hoja_ruta") Long id_hoja_ruta, Model model,
            HttpServletRequest request, HttpServletResponse response) {
        Documento documento = documentoService.findById(id_hoja_ruta);
        HojaRuta hojaRuta = hojaRutaService.findById(id_hoja_ruta);
        
        // Convertir java.util.Date a LocalDateTime
        Date fechaCreacionDate = hojaRuta.getFechaCreacion();
        LocalDateTime fechaCreacion = fechaCreacionDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        // Formatea la fecha y hora al formato deseado
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String fechaCreacionFormatted = fechaCreacion.format(outputFormatter);
        
        int year = fechaCreacion.getYear();
        model.addAttribute("hojaRuta", hojaRuta);
        
        if (hojaRuta.getTipo_derivacion().equals("Interno")) {
            Integer id_usuario_emisor = hojaRuta.getUsuario_emisor();
            Long id_usuario_emisor_long = id_usuario_emisor.longValue();
            Usuario usuario_emisor = usuarioService.findById(id_usuario_emisor_long);
            model.addAttribute("usuario_emisor", usuario_emisor);
            model.addAttribute("interno", true);
        }
        if (hojaRuta.getTipo_derivacion().equals("Externo")) {
            model.addAttribute("usuario_emisor", hojaRuta.getEntidadExterna().getNombre());
            model.addAttribute("externo", true);
        }
        
        String textoQR = "Hoja de Ruta: " + hojaRuta.getNroRuta() + "/" + year + "\n" +
                "Fecha: " + fechaCreacionFormatted + "\n" +
                "Identificador: " + generarAlfanumerico(hojaRuta.getId_hoja_ruta());
        model.addAttribute("textoQR", textoQR);
        
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariables(model.asMap());
        String htmlContent = templateEngine.process("seguimiento/hojaDeRuta", context);
        return ResponseEntity.ok().body(htmlContent);
    }

    @GetMapping("/buscar_documentoInicial/{nroRuta}")
    public ResponseEntity<?>  buscar_documentoInicial(@PathVariable(name = "nroRuta") int nroRuta, Model model, HttpServletRequest request,
    HttpServletResponse response) {
                String gestion = String.valueOf(LocalDate.now().getYear());
        // List<MovimientoDocumento> flujoDocumentos =
        // movimientoDocumentoService.obtener_Flujo_Documento(nroRuta);
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            Usuario user = usuarioService.findById(usuario.getId_usuario());
            Unidad unidad = user.getUnidad();
        HojaRuta hojaRuta = hojaRutaService.obtenerHojaRutaPorGestionUnidad(nroRuta, unidad.getId_unidad().intValue(), gestion);
        String fechaCreacionStr = hojaRuta.getFechaCreacion().toString(); // Supongamos que es una cadena como
                                                                          // '2024-08-21 16:55:29.393'

        // Usa LocalDateTime y ajusta el formato del DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime fechaCreacion = LocalDateTime.parse(fechaCreacionStr, formatter);

        // Formatea la fecha y hora al formato deseado
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm ");
        String fechaCreacionFormatted = fechaCreacion.format(outputFormatter);

        int year = fechaCreacion.getYear();

        model.addAttribute("hojaRuta", hojaRuta);
        if (hojaRuta.getTipo_derivacion().equals("Interno")) {
            Integer id_usuario_emisor = hojaRuta.getUsuario_emisor();
            Long id_usuario_emisor_long = id_usuario_emisor.longValue();
            Usuario usuario_emisor = usuarioService.findById(id_usuario_emisor_long);
            model.addAttribute("usuario_emisor", usuario_emisor);
            model.addAttribute("interno", true);
        }
        if (hojaRuta.getTipo_derivacion().equals("Externo")) {
            model.addAttribute("usuario_emisor", hojaRuta.getEntidadExterna().getNombre());
            model.addAttribute("externo", true);
        }

        String textoQR = "Hoja de Ruta: " + hojaRuta.getNroRuta() + "/" + year + "\n" +
                "Fecha: " + fechaCreacionFormatted + "\n" +
                "Identificador: " + generarAlfanumerico(hojaRuta.getId_hoja_ruta());
        model.addAttribute("textoQR", textoQR);

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariables(model.asMap());

        String htmlContent = templateEngine.process("seguimiento/hojaDeRuta", context);

        return ResponseEntity.ok().body(htmlContent);
    }
}
