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
import org.springframework.ui.Model;
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

    @GetMapping("/")
    public String inicio() {

        return "redirect:/Correspondencia";
    }

    @GetMapping("/Correspondencia")
    public String VentanaPrincipal(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("usuario") != null) {
            System.out.println("inicio sesion");
            Usuario user = (Usuario) request.getSession().getAttribute("usuario");
            Usuario usuario = usuarioService.findById(user.getId_usuario());
            System.out.println("USUARIO ACTIVO---" + usuario.getUsuario_nom());
            model.addAttribute("usuario", usuario);
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            return "index";
        } else {
            System.out.println("No inicio sesion");
            return "Login/login";
        }
    }

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

    @PostMapping("/LoginApiAdm")
    public ResponseEntity<String> RegistrarPersona(@RequestParam(name = "codFuncionario") String codFuncionario,
            @RequestParam(name = "ci") String ci, HttpServletRequest request) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int numeroDeMes = calendar.get(Calendar.MONTH) + 1;
        int gestion = calendar.get(Calendar.YEAR);

        try {
            

            Usuario usuario = usuarioService.obtener_Usuario(codFuncionario, ci);

            if (usuario != null && !usuario.getEstado().equals("X")) {

                HttpSession session = request.getSession(true);

                session.setAttribute("usuario", usuario);
                session.setAttribute("persona", usuario.getPersona());
                return ResponseEntity.ok("inicia");
            }

            // String url = "http://localhost:3333/api/londraPost/v1/obtenerDatos";
            String url = "http://virtual.uap.edu.bo:7174/api/londraPost/v1/obtenerDatos";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(
                    "{\"usuario\":\"" + codFuncionario + "\", \"contrasena\":\"" + ci + "\"}", headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (responseEntity.getStatusCodeValue() == 200) {
                Map<String, Object> data = responseEntity.getBody();

                Persona personaExistente = personaService.obtener_persona(ci);
                if (personaExistente == null) {
                    Persona nuevaPersona = new Persona();
                    nuevaPersona.setCi(data.get("per_num_doc").toString());
                    nuevaPersona.setNombre(data.get("per_nombres").toString());
                    nuevaPersona.setAp_paterno(data.get("per_ap_paterno").toString());
                    nuevaPersona.setAp_materno(data.get("per_ap_materno").toString());
                    nuevaPersona.setEstado("A");
                    personaService.save(nuevaPersona);
                    personaExistente = nuevaPersona; // Añadir esta línea para asignar la nueva persona creada
                }

                Unidad unidadExiste = unidadService.obtener_unidadPorNombre(data.get("eo_descripcion").toString());
                if (unidadExiste == null) {
                    unidadExiste = new Unidad();
                    unidadExiste.setEstado("A");
                    unidadExiste.setNombre(data.get("eo_descripcion").toString());
                    unidadService.save(unidadExiste);
                }

                Usuario usuarioExiste = usuarioService.obtener_Usuario(codFuncionario, ci);
                if (usuarioExiste == null) {
                    usuarioExiste = new Usuario();
                    usuarioExiste.setEstado("P");
                    usuarioExiste.setPersona(personaExistente);
                    usuarioExiste.setUnidad(unidadExiste);
                    usuarioExiste.setUsuario_nom(codFuncionario);
                    usuarioExiste.setContrasena(ci);
                    usuarioService.save(usuarioExiste);
                }
                HttpSession session = request.getSession(true);

                session.setAttribute("usuario", usuarioExiste);
                session.setAttribute("persona", usuarioExiste.getPersona());
                return ResponseEntity.ok("inicia");
            } else {
                return ResponseEntity.ok("Usuario Incorrecto o no registrado");
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.ok("Error al procesar la solicitud: Las credenciales son incorrectas");
        }
    }

    
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
