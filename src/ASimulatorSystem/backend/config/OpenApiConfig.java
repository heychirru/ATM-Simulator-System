package ASimulatorSystem.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI atmOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("ATM Simulator API")
                .version("v1")
                .description("Secure ATM account and transaction API."));
    }
}
