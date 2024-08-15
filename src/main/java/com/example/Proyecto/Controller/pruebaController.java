package com.example.Proyecto.Controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
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

@Controller
public class pruebaController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;

    @Autowired
	private SpringTemplateEngine templateEngine;

    Config config = new Config();

    @RequestMapping(value = "/recepcion", method = RequestMethod.GET)
    public String prueba() {

        return "busqueda/recepcion";
    }

    @PostMapping("/formularioDocumento")
	public ResponseEntity<?> formularioDocumento(@RequestParam(name = "nroRuta") String nroRuta, Model model, HttpServletRequest request,
			HttpServletResponse response) {

                Documento documento = documentoService.obtener_documento_hojaRuta(nroRuta);

                if (documento == null) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontró documento con el número de Hoja de Ruta proporcionado.");
                }

		model.addAttribute("documento", documento);
        model.addAttribute("unidades", unidadService.findAll());

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
            String arch = config.guardarArchivo((MultipartFile) archivo);

            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            long idUsuarioLong = usuario.getId_usuario();
            Integer idUsuarioInt = (int) idUsuarioLong;

            Documento documento = documentoService.findById(id_documento);
            Unidad unidadDestino = unidadService.findById(id_unidad_destino);

            MovimientoDocumento movimientoDocumento = new MovimientoDocumento();
            movimientoDocumento.setDocumento(documento);
            movimientoDocumento.setFechaHoraRegistro(new Date());
            movimientoDocumento.setUnidadDestino(unidadDestino);
            movimientoDocumento.setUnidadOrigen(usuario.getUnidad());
            movimientoDocumento.setUsuarioRegistro(idUsuarioInt);
            movimientoDocumento.setRuta_movimiento(arch);
            movimientoDocumento.setObservaciones(observacion);
            movimientoDocumentoService.save(movimientoDocumento);

            return ResponseEntity.ok("Registrado");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }



}
