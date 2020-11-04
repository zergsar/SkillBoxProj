package main.config;

import org.springframework.context.annotation.Configuration;
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
    registry.addResourceHandler("/favicon.ico")
        .addResourceLocations("/WEB-INF/favicon.ico");

  }


}