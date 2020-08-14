package main.service;

import main.model.repository.GlobalSettingsRepository;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GeneralService {

  private final GlobalSettingsRepository globalSettingsRepository;

  public GeneralService(GlobalSettingsRepository globalSettingsRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
  }

  public JSONObject getSettingsFromBase() {
    JSONObject response = new JSONObject();

    globalSettingsRepository.findAll().forEach(GlobalSettings ->
    {
      boolean value = false;
      if (GlobalSettings.getValue().equals("YES")) {
        value = true;
      }
      response.put(GlobalSettings.getCode(), value);
    });

    return response;
  }

}
