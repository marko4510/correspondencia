package com.example.Proyecto.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class pruebaController {
    

      @RequestMapping(value = "/prueba", method = RequestMethod.GET)
    public String prueba() {

        return "prueba";
    }
}
