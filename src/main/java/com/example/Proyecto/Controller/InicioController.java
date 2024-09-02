package com.example.Proyecto.Controller;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.example.Proyecto.Model.Cargo;
import com.example.Proyecto.Model.Persona;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.CargoService;
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


@Controller
public class InicioController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UnidadService unidadService;
    @RequestMapping(value = "/inicio", method = RequestMethod.GET)
    public String inicio(HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {

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
    public ResponseEntity< ?> actualizarPersonas() throws IOException, InterruptedException {
      
        String url = "http://192.168.10.93:3333/api/londraPost/v1/personasLondra/obtenerDatos";

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

                
                Cargo cargo = cargoService.obtener_cargoPorNombre(data.get("puesto").toString());
                if (cargo == null) {
                    cargo = new Cargo();
                    cargo.setEstado("A");
                    cargo.setNombre(data.get("puesto").toString());
                    cargoService.save(cargo);
                }

                Unidad unidad = unidadService.obtener_unidadPorNombre(data.get("direccion").toString());
                if (unidad == null) {
                    unidad = new Unidad();
                    unidad.setEstado("A");
                    unidad.setNombre(data.get("direccion").toString());
                    unidadService.save(unidad);
                }
                
                Persona persona = personaService.obtener_persona(data.get("ci").toString());
                if (persona == null) {
                    persona = new Persona();
                    persona.setNombre(nombres);
                    persona.setAp_paterno(apPaterno);
                    persona.setAp_materno(apMaterno);
                    persona.setCi(ciPersona);
                    persona.setCargo(cargo);
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
                }
                
            }
            System.out.println(datos.length);
        }
        return ResponseEntity.ok("ok");
    }
    

}
