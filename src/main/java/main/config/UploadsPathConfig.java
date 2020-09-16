package main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class UploadsPathConfig extends WebMvcConfigurationSupport {

//  @Value("${default.upload.dir}")
//  private String defaultUploadDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/css/**")
        .addResourceLocations("/WEB-INF/css/");
    registry.addResourceHandler("/fonts/**")
        .addResourceLocations("/WEB-INF/fonts/");
    registry.addResourceHandler("/img/**")
        .addResourceLocations("/WEB-INF/img/");
    registry.addResourceHandler("/js/**")
        .addResourceLocations("/WEB-INF/js/");
    registry.addResourceHandler("/upload/**")
        .addResourceLocations("/upload/");
  }



}