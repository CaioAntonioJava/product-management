package com.caio.product_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Produtos")
                        .description("""
                                API REST para gerenciamento de produtos e categorias.

                                Permite cadastrar, listar, atualizar (total ou parcialmente) e excluir
                                produtos e categorias, além de consultar produtos filtrando por nome
                                ou por categoria.

                                Todas as mensagens de erro retornadas pela API estão em português (PT-BR).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Caio Antônio")
                                .url("https://github.com/CaioAntonioJava"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor local de desenvolvimento")
                ));
    }
}
