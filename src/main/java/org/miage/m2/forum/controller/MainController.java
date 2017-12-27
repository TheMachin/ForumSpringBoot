package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.SignUpForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping(value="/")
    public String index(Map<String, Object> model){
        return "index";
    }

    @GetMapping(value = "/formProject")
    public String formProject(Model model){
        return "index";
    }

}
