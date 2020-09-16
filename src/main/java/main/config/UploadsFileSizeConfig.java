package main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class UploadsFileSizeConfig {
  
  public MultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver
        = new CommonsMultipartResolver();
    multipartResolver.setMaxUploadSize(20485760);
    return multipartResolver;
  }

}
