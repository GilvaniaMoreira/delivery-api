package com.deliverytech.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "DeliveryTech API", version = "v1.0.0", description = "API RESTful para sistema de delivery — gerenciamento de clientes, restaurantes, produtos e pedidos.", contact = @Contact(name = "DeliveryTech", email = "contato@deliverytech.com")), servers = @Server(url = "http://localhost:8080", description = "Servidor local"), security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", description = "Insira o token JWT obtido no endpoint /api/auth/login. Formato: Bearer {token}", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

}