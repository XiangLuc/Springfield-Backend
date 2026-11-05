package fr.efrei2026.springfield.utiles.configurations;

import fr.efrei2026.springfield.shared.dto.ErreurApi;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    private static final String SCHEMA_ERREUR = "ErreurApi";
    private static final String PREFIXE_ERREUR_CLIENT = "4";

    @Bean
    public OpenAPI documentationApi() {
        return new OpenAPI().info(new Info()
                .title("API Springfield")
                .version("1.0")
                .description("Supervision des équipements du réseau et suivi des incidents. "
                        + "Pour tester les routes protégées, connectez-vous d'abord avec POST /api/auth/connexion."));
    }

    @Bean
    public OpenApiCustomizer formatDesErreurs() {
        return api -> {
            api.getComponents().addSchemas(SCHEMA_ERREUR, ModelConverters.getInstance().read(ErreurApi.class).get(SCHEMA_ERREUR));
            api.getPaths().values().forEach(chemin -> chemin.readOperations().forEach(operation ->
                    operation.getResponses().forEach((code, reponse) -> {
                        if (code.startsWith(PREFIXE_ERREUR_CLIENT)) {
                            reponse.setContent(contenuErreur());
                        }
                    })));
        };
    }

    private Content contenuErreur() {
        return new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                new MediaType().schema(new Schema<ErreurApi>().$ref(SCHEMA_ERREUR)));
    }
}
