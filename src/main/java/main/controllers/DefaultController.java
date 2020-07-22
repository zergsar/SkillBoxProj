package main.controllers;

import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String index(Model model)
    {
        return "index";
    }
}
