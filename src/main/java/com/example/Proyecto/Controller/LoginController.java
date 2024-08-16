package com.example.Proyecto.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto.Model.Persona;
import com.example.Proyecto.Model.Unidad;
import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.PersonaService;
import com.example.Proyecto.Service.UnidadService;
import com.example.Proyecto.Service.UsuarioService;



@Controller
public class LoginController {
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private UnidadService unidadService;

    @GetMapping("/login")
    public String login() {

        return "Login/login";
    }
    
    @PostMapping("/login_form")
    public String login_form(@RequestParam(name = "user")String user,
    @RequestParam(name = "contrasena")String contrasena,HttpServletRequest request) {
      
        Usuario usuario = usuarioService.obtener_Usuario(user, contrasena);

        if (usuario != null && !usuario.getEstado().equals("X")) {
            HttpSession session = request.getSession(true);

            session.setAttribute("usuario", usuario);
            session.setAttribute("persona", usuario.getPersona());
            return "redirect:/inicio";
        }else{
            return "redirect:/login";
        }
        
    }

    // @PostMapping("/RegistrarPersona")
    // public ResponseEntity<String> RegistrarPersona(@RequestParam(name = "codFuncionario") String codFuncionario,
    //         @RequestParam(name = "Ci") String Ci) {
    //     Calendar calendar = Calendar.getInstance();
    //     calendar.setTime(new Date());
    //     int numeroDeMes = calendar.get(Calendar.MONTH) + 1;
    //     int gestion = calendar.get(Calendar.YEAR);
      
    //     try {
      
    //         String url = "http://172.16.21.2:3333/api/londraPost/v1/obtenerDatos";
    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);
    //         HttpEntity<String> requestEntity = new HttpEntity<>(
    //                 "{\"usuario\":\"" + codFuncionario + "\", \"contrasena\":\"" + Ci + "\"}", headers);
    //         RestTemplate restTemplate = new RestTemplate();
    //         ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);


    //         if (responseEntity.getStatusCodeValue() == 200) {
    //             Map<String, Object> data = responseEntity.getBody();

               
    //             Persona personaExistente = personaService.obtener_persona(Ci);
    //             if (personaExistente == null) {
    //                 Persona nuevaPersona = new Persona();
    //                 nuevaPersona.setCi(data.get("per_num_doc").toString());
    //                 nuevaPersona.setNombre(data.get("per_nombres").toString());
    //                 nuevaPersona.setAp_paterno(data.get("per_ap_paterno").toString());
    //                 nuevaPersona.setAp_materno(data.get("per_ap_materno").toString());
    //                 nuevaPersona.setEstado("A");
    //                 personaService.save(nuevaPersona);


    //             }
    //             Unidad unidadExiste = unidadService.obtener_unidadPorNombre(data.get("eo_descripcion").toString());

    //             if (unidadExiste == null) {
    //                 unidadExiste = new Unidad();
    //                 unidadExiste.setEstado("A");
    //                 unidadExiste.setNombre(data.get("eo_descripcion").toString());
    //                 unidadService.save(unidadExiste);
    //             }

    //             Usuario usuarioExiste = usuarioService.obtener_Usuario(codFuncionario, Ci);

    //             if (usuarioExiste == null) {
    //             usuarioExiste = new Usuario();
    //             usuarioExiste.setEstado("P");
    //             usuarioExiste.setPersona(personaExistente);
    //             usuarioExiste.setUnidad(unidadExiste);
    //             usuarioExiste.setUsuario_nom(codFuncionario);
    //             usuarioExiste.setContrasena(Ci);
    //             usuarioService.save(usuarioExiste);
    //             }

                

    //                 return ResponseEntity.ok("Se creó un nuevo registro correctamente");
    //             }
    //         } else {
    //             return ResponseEntity.ok("No se pudo obtener los datos del funcionario con el código proporcionado");
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.ok("Error al procesar la solicitud");
    //     }
    
    @RequestMapping(value = "/cerrar_sesion",method = RequestMethod.GET)
    public String cerrarSesion(HttpServletRequest request, RedirectAttributes flash){
        HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			flash.addAttribute("validado", "Se cerro sesion con exito!");
		}
		return "redirect:/login";
    }
}
