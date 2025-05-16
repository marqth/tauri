package fr.eseo.tauri.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map "/"
        String forwardIndexString = "forward:/index.html";
        registry.addViewController("/")
                .setViewName(forwardIndexString);

        // Single directory level - no need to exclude "api"
        registry.addViewController("/{x:[\\w\\-]+}")
                .setViewName(forwardIndexString);
        // Multi-level directory path, need to exclude "api" on the first part of the path
        registry.addViewController("/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}")
                .setViewName(forwardIndexString);
    }

}
