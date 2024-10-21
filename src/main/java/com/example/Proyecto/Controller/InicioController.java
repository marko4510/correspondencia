package com.example.Proyecto.Controller;

import java.time.LocalDate;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.example.Proyecto.Model.Cargo;
import com.example.Proyecto.Model.HojaRuta;
import com.example.Proyecto.Model.MovimientoDocumento;
import com.example.Proyecto.Model.Persona;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.CargoService;
import com.example.Proyecto.Service.DocumentoService;
import com.example.Proyecto.Service.HojaRutaService;
import com.example.Proyecto.Service.MovimientoDocumentoService;
import com.example.Proyecto.Service.PersonaService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class InicioController {

    @Autowired
    private HojaRutaService hojaRutaService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private MovimientoDocumentoService movimientoDocumentoService;
    @RequestMapping(value = "/inicio", method = RequestMethod.GET)
    public String inicio(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("usuario") != null) {
           
            Usuario user = (Usuario) request.getSession().getAttribute("usuario");
            Usuario usuario = usuarioService.findById(user.getId_usuario());
            String gestion = String.valueOf(LocalDate.now().getYear());
            model.addAttribute("usuario", usuario);
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            Unidad unidad = user.getUnidad();

            List<MovimientoDocumento> movimientoDocumentosSolicitados = movimientoDocumentoService.ListaMovimientosSolicitados(unidad.getId_unidad().intValue());
            List<MovimientoDocumento> movimientoArchivados = movimientoDocumentoService.Lista_Archivados(unidad.getId_unidad(),gestion);
        
            model.addAttribute("movimientoDocumentosSolicitados", movimientoDocumentosSolicitados);
            model.addAttribute("numSolicitud", movimientoDocumentosSolicitados.size());
            model.addAttribute("documentosUnidad", documentoService.obtener_DocumentosPorUnidadYGestion(user.getUnidad().getId_unidad().intValue(), gestion));
            model.addAttribute("hojasRutas", hojaRutaService.ObtenerHojasDeRutaPorUnidadyGestion(unidad.getId_unidad().intValue(), gestion));
            model.addAttribute("archivados", movimientoArchivados);
            model.addAttribute("opcion", "Menu_principal");
            
            return "index";
        }else{
            return "redirect:/login";
        }
        
    }


    // @RequestMapping(value = "/login", method = RequestMethod.GET)
    // public String login() {

    //     return "login";
    // }

    @PostMapping("/actualizarPersonas")
    public ResponseEntity< ?> actualizarPersonas() throws IOException, InterruptedException, ParseException {
      
        String url = "http://virtual.uap.edu.bo:7174/api/londraPost/v1/personasLondra/obtenerDatos";

        // Crear encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear la entidad de la solicitud con los encabezados
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        // Crear RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Realizar la solicitud GET
        ResponseEntity<Map[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map[].class);

        // Obtener el cuerpo de la respuesta (una lista de Mapas)
        Map<String, Object>[] datos = responseEntity.getBody();

        if (datos != null && datos.length > 0) {
            for (Map<String, Object> data : datos) {

                // Acceder a los datos específicos
                String Per_id = data.get("Per_id").toString();
                String nombres = data.get("nombres").toString();
                String apPaterno = data.get("ap_1").toString();
                String apMaterno = data.get("ap_2").toString();
                String ciPersona = data.get("ci").toString();
                String direccion = data.get("direccion").toString();
                String puesto = data.get("puesto").toString();

                String url2 = "http://virtual.uap.edu.bo:7174/api/londraPost/v1/personaLondra/obtenerDatos";
                HttpHeaders headers2 = new HttpHeaders();
                headers2.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity2 = new HttpEntity<>(
                        "{\"usuario\":\"" + Per_id + "\"}", headers);
                RestTemplate restTemplate2 = new RestTemplate();
                ResponseEntity<Map> responseEntity2 = restTemplate2.exchange(url2, HttpMethod.POST, requestEntity2, Map.class);

                if (responseEntity2.getStatusCodeValue() == 200) {
                    Map<String, Object> data2 = responseEntity2.getBody();

                
                Cargo cargo = cargoService.obtener_cargoPorNombre(data.get("puesto").toString());
                if (cargo == null) {
                    cargo = new Cargo();
                    cargo.setEstado("A");
                    cargo.setNombre(data.get("puesto").toString());
                    cargoService.save(cargo);
                }else{
                    cargo.setNombre(data.get("puesto").toString());
                    cargoService.save(cargo);
                }

                String direccion2 = data.get("direccion").toString();

                // Verificar si la dirección contiene "(E)" al final y removerlo
                if (direccion2.endsWith("(E)")) {
                    // Eliminar el "(E)" al final de la dirección
                    direccion2 = direccion2.substring(0, direccion2.length() - 3).trim();
                }

                Unidad unidad = unidadService.obtener_unidadPorNombre(direccion2);
                if (unidad == null) {
                    unidad = new Unidad();
                    unidad.setEstado("A");
                    unidad.setNombre(direccion2);
                    unidad.setContadorCite(0);
                    unidad.setContadorHojaRuta(0);
                    unidadService.save(unidad);
                }else{
                    unidad.setNombre(direccion2);
                    if (unidad.getContadorCite() == null && unidad.getContadorHojaRuta() == null) {
                        unidad.setContadorCite(0);
                        unidad.setContadorHojaRuta(0);
                    }
                    unidadService.save(unidad);
                }
                
                Persona persona = personaService.obtener_persona(data.get("ci").toString());
                if (persona == null) {
                    persona = new Persona();
                    persona.setNombre(nombres);
                    persona.setEstado("P");
                    persona.setAp_paterno(apPaterno);
                    persona.setAp_materno(apMaterno);
                    persona.setCi(ciPersona);
                    persona.setCargo(cargo);
                    persona.setCorreo(data2.get("perd_email_personal").toString());
                    persona.setNumero_contacto(data2.get("perd_celular").toString());
                    persona.setSexo(data2.get("per_sexo").toString());
             
                    String fechaNacStr = data2.get("fecha_nac").toString();  // Fecha en formato recibido
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date fechaNac = formatter.parse(fechaNacStr);
                    persona.setFecha_nacimiento(fechaNac);
                    personaService.save(persona);
                }else{
                    persona.setNombre(nombres);
                    persona.setAp_paterno(apPaterno);
                    persona.setAp_materno(apMaterno);
                    persona.setCargo(cargo);
                    persona.setCorreo(data2.get("perd_email_personal").toString());
                    persona.setNumero_contacto(data2.get("perd_celular").toString());
                    persona.setSexo(data2.get("per_sexo").toString());
                    String fechaNacStr = data2.get("fecha_nac").toString();  // Fecha en formato recibido
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date fechaNac = formatter.parse(fechaNacStr);
                    persona.setFecha_nacimiento(fechaNac);
                    personaService.save(persona);
                }

                Usuario usuario = usuarioService.obtener_Usuario_CiPersona(ciPersona);
                if (usuario == null) {
                    usuario = new Usuario();
                    usuario.setEstado("P");
                    usuario.setPersona(persona);
                    usuario.setUnidad(unidad);
                    usuario.setUsuario_nom(data.get("Per_id").toString());
                    usuario.setContrasena(persona.getCi());
                    usuarioService.save(usuario);
                }else{
                    usuario.setUnidad(unidad);
                    usuario.setPersona(persona);
                    usuarioService.save(usuario);
                }
            }
            }
            System.out.println(datos.length);
        }
        return ResponseEntity.ok("ok");
    }
    

}
