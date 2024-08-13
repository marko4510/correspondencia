package com.example.Proyecto.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InicioController {



    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String inicio() {

        return "index";
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {

        return "login";
    }

}
