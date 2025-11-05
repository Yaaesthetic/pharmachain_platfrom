package ma.pharmachain.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    /**
     * Get the current authenticated user's Keycloak ID
     */
    public String getCurrentKeycloakUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getSubject(); // 'sub' claim contains Keycloak user ID
        }
        throw new IllegalStateException("User is not authenticated");
    }

    /**
     * Get the current authenticated username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getClaim("preferred_username");
        }
        throw new IllegalStateException("User is not authenticated");
    }

    /**
     * Get a specific claim from JWT token
     */
    public <T> T getClaim(String claimName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getClaim(claimName);
        }
        throw new IllegalStateException("User is not authenticated");
    }

    /**
     * Get the entire JWT token
     */
    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getToken();
        }
        throw new IllegalStateException("User is not authenticated");
    }

    /**
     * Get user's code from JWT attributes
     */
    public String getCurrentUserCode() {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaimAsString("code");
    }

    /**
     * Get user's email
     */
    public String getCurrentUserEmail() {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaim("email");
    }
}
