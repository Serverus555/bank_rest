package com.example.bankcards.config;

import com.example.bankcards.util.security.AuthenticatedUserId;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String SCHEMA_NAME = "jwtAuth";

    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthenticatedUserId.class);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(SCHEMA_NAME))
            .components(new Components().addSecuritySchemes(SCHEMA_NAME,
                new SecurityScheme()
                    .name(SCHEMA_NAME)
                    .type(SecurityScheme.Type.OAUTH2)
                    .flows(getFlows())));
    }

    private static OAuthFlows getFlows() {
        return new OAuthFlows()
            .password(
                new OAuthFlow()
                    .tokenUrl("/auth/login")
                    .scopes(new Scopes())
            );
    }
}
