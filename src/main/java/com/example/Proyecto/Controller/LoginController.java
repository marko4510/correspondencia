package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto.Model.Usuario;
import com.example.Proyecto.Service.UsuarioService;



@Controller
public class LoginController {
    
    @Autowired
    private UsuarioService usuarioService;

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
