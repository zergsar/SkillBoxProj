package main.controllers;

import main.response.Info;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ApiGeneralController {

   @GetMapping("/api/init/")
    public String init()
    {
        Info info = new Info();
        return convertToJson(info);
    }


    private String convertToJson(Object obj)
    {
        ObjectMapper mapper = new ObjectMapper();

        String objStr = null;

        try {
            objStr = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objStr;
    }


}
