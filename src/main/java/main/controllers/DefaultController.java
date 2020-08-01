package main.controllers;

import main.model.GlobalSettings;
import main.model.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;


@Controller
public class DefaultController {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @RequestMapping("/")
    public String index(Model model)
    {
        return "index";
    }

}
