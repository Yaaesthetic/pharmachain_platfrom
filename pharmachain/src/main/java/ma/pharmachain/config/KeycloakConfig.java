package ma.pharmachain.config;

import lombok.Getter;
import lombok.Setter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfig {

    private String authServerUrl;
    private String realm;
    private String resource;
    private Admin admin;

    @Getter
    @Setter
    public static class Admin {
        private String clientId;
        private String clientSecret;
    }

    /**
     * Keycloak Admin Client for managing users programmatically
     * Uses CLIENT_CREDENTIALS grant type with service account
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(admin.getClientId())
                .clientSecret(admin.getClientSecret())
                .build();
    }
}
