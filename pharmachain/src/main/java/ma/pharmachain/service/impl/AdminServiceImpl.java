package ma.pharmachain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.pharmachain.dto.AdminCreateRequest;
import ma.pharmachain.dto.AdminUpdateRequest;
import ma.pharmachain.entity.Admin;
import ma.pharmachain.exception.ResourceNotFoundException;
import ma.pharmachain.repository.AdminRepository;
import ma.pharmachain.service.AdminService;
import ma.pharmachain.service.KeycloakAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Override
    @Transactional(readOnly = true)
    public Admin getAdminByKeycloakUserId(String keycloakUserId) {
        return adminRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with Keycloak ID: " + keycloakUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Admin> listAdmins(int page, int size) {
        return adminRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + id));
    }

    @Override
    public Admin createAdmin(AdminCreateRequest request) {
        // Validate unique constraints
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (adminRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Admin code already exists: " + request.getCode());
        }

        try {
            // Step 1: Create user in Keycloak first
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("code", List.of(request.getCode()));
            attributes.put("userType", List.of("ADMIN"));

            String keycloakUserId = keycloakAdminService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    attributes
            );

            // Step 2: Assign ADMIN role in Keycloak
            keycloakAdminService.assignRoleToUser(keycloakUserId, "ADMIN");

            // Step 3: Create admin in local database
            Admin admin = new Admin();
            admin.setKeycloakUserId(keycloakUserId);
            admin.setCode(request.getCode());
            admin.setUsername(request.getUsername());
            admin.setIsActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setSyncedAt(LocalDateTime.now());

            Admin savedAdmin = adminRepository.save(admin);
            log.info("Admin created successfully: {} with Keycloak ID: {}",
                    request.getUsername(), keycloakUserId);

            return savedAdmin;

        } catch (Exception e) {
            log.error("Error creating admin: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create admin: " + e.getMessage(), e);
        }
    }

    @Override
    public Admin updateAdmin(Long id, AdminUpdateRequest request) {
        Admin admin = getAdminById(id);

        try {
            // Update in Keycloak if email or name changed
            if (request.getEmail() != null || request.getFirstName() != null ||
                    request.getLastName() != null) {
                keycloakAdminService.updateUser(
                        admin.getKeycloakUserId(),
                        request.getEmail(),
                        request.getFirstName(),
                        request.getLastName()
                );
            }

            // Update password if provided
            if (request.getPassword() != null) {
                keycloakAdminService.resetPassword(admin.getKeycloakUserId(), request.getPassword());
            }

            // Update username in local DB
            if (request.getUsername() != null) {
                if (!request.getUsername().equals(admin.getUsername()) &&
                        adminRepository.existsByUsername(request.getUsername())) {
                    throw new IllegalArgumentException("Username already exists: " + request.getUsername());
                }
                admin.setUsername(request.getUsername());
            }

            // Update code in local DB
            if (request.getCode() != null) {
                if (!request.getCode().equals(admin.getCode()) &&
                        adminRepository.existsByCode(request.getCode())) {
                    throw new IllegalArgumentException("Admin code already exists: " + request.getCode());
                }
                admin.setCode(request.getCode());

                // Update code in Keycloak attributes
                Map<String, List<String>> attributes = new HashMap<>();
                attributes.put("code", List.of(request.getCode()));
                keycloakAdminService.updateUserAttributes(admin.getKeycloakUserId(), attributes);
            }

            // Update active status
            if (request.getIsActive() != null) {
                admin.setIsActive(request.getIsActive());
                keycloakAdminService.setUserEnabled(admin.getKeycloakUserId(), request.getIsActive());
            }

            admin.setSyncedAt(LocalDateTime.now());
            return adminRepository.save(admin);

        } catch (Exception e) {
            log.error("Error updating admin: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update admin: " + e.getMessage(), e);
        }
    }

    @Override
    public Admin partialUpdateAdmin(Long id, Map<String, Object> updates) {
        Admin admin = getAdminById(id);

        try {
            // Apply partial updates
            updates.forEach((key, value) -> {
                switch (key) {
                    case "username":
                        String newUsername = value.toString();
                        if (!newUsername.equals(admin.getUsername()) &&
                                adminRepository.existsByUsername(newUsername)) {
                            throw new IllegalArgumentException("Username already exists: " + newUsername);
                        }
                        admin.setUsername(newUsername);
                        break;
                    case "password":
                        keycloakAdminService.resetPassword(admin.getKeycloakUserId(), value.toString());
                        break;
                    case "code":
                        String newCode = value.toString();
                        if (!newCode.equals(admin.getCode()) &&
                                adminRepository.existsByCode(newCode)) {
                            throw new IllegalArgumentException("Admin code already exists: " + newCode);
                        }
                        admin.setCode(newCode);

                        // Update in Keycloak attributes
                        Map<String, List<String>> attributes = new HashMap<>();
                        attributes.put("code", List.of(newCode));
                        keycloakAdminService.updateUserAttributes(admin.getKeycloakUserId(), attributes);
                        break;
                    case "isActive":
                        Boolean isActive = Boolean.valueOf(value.toString());
                        admin.setIsActive(isActive);
                        keycloakAdminService.setUserEnabled(admin.getKeycloakUserId(), isActive);
                        break;
                }
            });

            admin.setSyncedAt(LocalDateTime.now());
            return adminRepository.save(admin);

        } catch (Exception e) {
            log.error("Error partially updating admin: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAdmin(Long id) {
        Admin admin = getAdminById(id);

        try {
            // Delete from Keycloak first
            keycloakAdminService.deleteUser(admin.getKeycloakUserId());

            // Then delete from local database
            adminRepository.deleteById(id);

            log.info("Admin deleted successfully: {}", admin.getUsername());
        } catch (Exception e) {
            log.error("Error deleting admin: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete admin: " + e.getMessage(), e);
        }
    }
}
