package main.service;

import main.model.GlobalSettingsRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralService {

    @Autowired
    private AuthService authService;

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public JSONObject getSettingsFromBase()
    {
        JSONObject response = new JSONObject();

        globalSettingsRepository.findAll().forEach(GlobalSettings ->
        {
            boolean value = false;
            if(GlobalSettings.getValue().equals("YES"))
            {
                value = true;
            }
            response.put(GlobalSettings.getCode(), value);
        });

        return response;
    }

}
