package com.example.Proyecto.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InicioController {



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

}
