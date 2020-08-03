package main.controllers;

import main.model.GlobalSettingsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DefaultController {

  private final GlobalSettingsRepository globalSettingsRepository;

  public DefaultController(GlobalSettingsRepository globalSettingsRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
  }

  @RequestMapping("/")
  public String index(Model model) {
    return "index";
  }

}
