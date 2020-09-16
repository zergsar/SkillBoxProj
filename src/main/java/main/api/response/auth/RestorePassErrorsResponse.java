package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestorePassErrorsResponse implements Serializable {

  private String code;
  private String password;
  private String captcha;



}
