package ma.pharmachain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.pharmachain.config.KeycloakConfig;
import ma.pharmachain.exception.KeycloakOperationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakConfig keycloakConfig;

    /**
     * Creates a new user in Keycloak and returns the Keycloak user ID
     */
    public String createUser(String username, String email, String password,
                             String firstName, String lastName, Map<String, List<String>> attributes) {
        try {
            UsersResource usersResource = getUsersResource();

            // Check if user already exists
            List<UserRepresentation> existingUsers = usersResource.search(username, true);
            if (!existingUsers.isEmpty()) {
                throw new KeycloakOperationException("User already exists: " + username);
            }

            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailVerified(true);

            // Add custom attributes
            if (attributes != null && !attributes.isEmpty()) {
                user.setAttributes(attributes);
            }

            // Create password credential
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            // Create user in Keycloak
            Response response = usersResource.create(user);

            try {
                if (response.getStatus() == 201) {
                    // Extract user ID from Location header
                    String locationHeader = response.getHeaderString("Location");
                    String userId = extractUserIdFromLocation(locationHeader);

                    log.info("User created successfully in Keycloak: {} with ID: {}", username, userId);
                    return userId;
                } else {
                    String errorMessage = response.readEntity(String.class);
                    throw new KeycloakOperationException("Failed to create user. Status: " +
                            response.getStatus() + ", Message: " + errorMessage);
                }
            } finally {
                response.close();
            }
        } catch (KeycloakOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error creating user in Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Assigns a role to a user in Keycloak
     */
    public void assignRoleToUser(String userId, String roleName) {
        try {
            UsersResource usersResource = getUsersResource();
            UserResource userResource = usersResource.get(userId);
            RealmResource realmResource = getRealmResource();

            // Get role representation
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

            if (role == null) {
                throw new KeycloakOperationException("Role not found: " + roleName);
            }

            // Assign role to user
            userResource.roles().realmLevel().add(Collections.singletonList(role));

            log.info("Role {} assigned to user ID: {}", roleName, userId);
        } catch (Exception e) {
            log.error("Error assigning role to user: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error assigning role to user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates user information in Keycloak
     */
    public void updateUser(String userId, String email, String firstName, String lastName) {
        try {
            UsersResource usersResource = getUsersResource();
            UserResource userResource = usersResource.get(userId);

            UserRepresentation user = userResource.toRepresentation();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            userResource.update(user);

            log.info("User updated successfully in Keycloak: {}", userId);
        } catch (Exception e) {
            log.error("Error updating user in Keycloak: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error updating user in Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Resets user password in Keycloak
     */
    public void resetPassword(String userId, String newPassword) {
        try {
            UsersResource usersResource = getUsersResource();
            UserResource userResource = usersResource.get(userId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            userResource.resetPassword(credential);

            log.info("Password reset successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error resetting password: " + e.getMessage(), e);
        }
    }

    /**
     * Enables or disables a user in Keycloak
     */
    public void setUserEnabled(String userId, boolean enabled) {
        try {
            UsersResource usersResource = getUsersResource();
            UserResource userResource = usersResource.get(userId);

            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);

            userResource.update(user);

            log.info("User {} status changed to: {}", userId, enabled ? "enabled" : "disabled");
        } catch (Exception e) {
            log.error("Error updating user status: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error updating user status: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user from Keycloak
     */
    public void deleteUser(String userId) {
        try {
            UsersResource usersResource = getUsersResource();
            Response response = usersResource.delete(userId);

            try {
                if (response.getStatus() == 204) {
                    log.info("User deleted successfully from Keycloak: {}", userId);
                } else {
                    throw new KeycloakOperationException("Failed to delete user. Status: " + response.getStatus());
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error deleting user from Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Gets user by Keycloak user ID
     */
    public UserRepresentation getUserById(String userId) {
        try {
            UsersResource usersResource = getUsersResource();
            return usersResource.get(userId).toRepresentation();
        } catch (Exception e) {
            log.error("Error fetching user from Keycloak: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error fetching user from Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Updates user custom attributes
     */
    public void updateUserAttributes(String userId, Map<String, List<String>> attributes) {
        try {
            UsersResource usersResource = getUsersResource();
            UserResource userResource = usersResource.get(userId);

            UserRepresentation user = userResource.toRepresentation();
            user.setAttributes(attributes);

            userResource.update(user);

            log.info("User attributes updated successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error updating user attributes: {}", e.getMessage(), e);
            throw new KeycloakOperationException("Error updating user attributes: " + e.getMessage(), e);
        }
    }

    // Helper methods

    private UsersResource getUsersResource() {
        return keycloakAdminClient.realm(keycloakConfig.getRealm()).users();
    }

    private RealmResource getRealmResource() {
        return keycloakAdminClient.realm(keycloakConfig.getRealm());
    }

    private String extractUserIdFromLocation(String locationHeader) {
        if (locationHeader == null || locationHeader.isEmpty()) {
            throw new KeycloakOperationException("Location header is empty");
        }

        // Location format: http://localhost:8180/admin/realms/spring-boot-realm/users/USER_ID
        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }
}
