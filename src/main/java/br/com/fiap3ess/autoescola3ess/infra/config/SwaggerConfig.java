package br.com.fiap3ess.autoescola3ess.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI autoEscolaOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API AutoEscola3ESS").version("1.0")
                        .description("SOA e WebServices: alunos, instrutores, usuários, instruções e consulta ViaCEP. "
                                + "Faça login e informe o JWT em Authorize. Gerenciamento de usuários exige ADMIN."))
                .components(new Components().addSecuritySchemes("bearer-key", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
