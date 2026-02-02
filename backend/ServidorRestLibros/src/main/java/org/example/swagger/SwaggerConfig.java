package org.example.swagger;

//imports correspondientes

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuración Swagger para la generación de documentación de la API REST
 * DOCUMENTACIÓN EN:
 * http://localhost:8080/swagger-ui/
 * (donde localhost:8080 es la URL base de la aplicación)
 */

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookEx API")
                        .version("1.0")
                        .description("Documentación de la API de BookEx"));
    }
}