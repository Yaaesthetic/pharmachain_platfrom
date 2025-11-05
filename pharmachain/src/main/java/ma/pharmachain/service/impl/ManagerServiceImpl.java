package ma.pharmachain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.pharmachain.dto.ManagerCreateRequest;
import ma.pharmachain.dto.ManagerUpdateRequest;
import ma.pharmachain.exception.ResourceNotFoundException;
import ma.pharmachain.entity.*;
import ma.pharmachain.repository.*;
import ma.pharmachain.service.ManagerService;
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
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final ClientRepository clientRepository;
    private final BordereauRepository bordereauxRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Override
    @Transactional(readOnly = true)
    public Manager getManagerByKeycloakUserId(String keycloakUserId) {
        return managerRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with Keycloak ID: " + keycloakUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Manager> listManagers(int page, int size) {
        return managerRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Manager getManagerByCode(String code) {
        return managerRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + code));
    }

    @Override
    public Manager createManager(ManagerCreateRequest request) {
        // Validate unique constraints
        if (managerRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Manager code already exists: " + request.getCode());
        }
        if (managerRepository.existsBySecteurName(request.getSecteurName())) {
            throw new IllegalArgumentException("Secteur name already exists: " + request.getSecteurName());
        }

        // Validate assignedAdmin is required
        if (request.getAssignedAdminCode() == null) {
            throw new IllegalArgumentException("AssignedAdmin is required");
        }

        // Find assignedAdmin
        Admin admin = adminRepository.findByCode(request.getAssignedAdminCode())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + request.getAssignedAdminCode()));

        try {
            // Step 1: Create user in Keycloak
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("code", List.of(request.getCode()));
            attributes.put("userType", List.of("MANAGER"));
            attributes.put("secteurName", List.of(request.getSecteurName()));
            attributes.put("phone", List.of(request.getPhone()));
            attributes.put("address", List.of(request.getAddress()));

            String keycloakUserId = keycloakAdminService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    attributes
            );

            // Step 2: Assign MANAGER role in Keycloak
            keycloakAdminService.assignRoleToUser(keycloakUserId, "MANAGER");

            // Step 3: Create manager in local database
            Manager manager = new Manager();
            manager.setKeycloakUserId(keycloakUserId);
            manager.setCode(request.getCode());
            manager.setUsername(request.getUsername());
            manager.setSecteurName(request.getSecteurName());
            manager.setPhone(request.getPhone());
            manager.setAddress(request.getAddress());
            manager.setAssignedAdmin(admin);
            manager.setIsActive(true);
            manager.setCreatedAt(LocalDateTime.now());
            manager.setSyncedAt(LocalDateTime.now());

            Manager savedManager = managerRepository.save(manager);
            log.info("Manager created successfully: {} with Keycloak ID: {}",
                    request.getUsername(), keycloakUserId);

            return savedManager;

        } catch (Exception e) {
            log.error("Error creating manager: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create manager: " + e.getMessage(), e);
        }
    }

    @Override
    public Manager updateManager(String code, ManagerUpdateRequest request) {
        Manager manager = getManagerByCode(code);

        try {
            // Update in Keycloak if needed
            if (request.getEmail() != null || request.getFirstName() != null ||
                    request.getLastName() != null) {
                keycloakAdminService.updateUser(
                        manager.getKeycloakUserId(),
                        request.getEmail(),
                        request.getFirstName(),
                        request.getLastName()
                );
            }

            // Update password if provided
            if (request.getPassword() != null) {
                keycloakAdminService.resetPassword(manager.getKeycloakUserId(), request.getPassword());
            }

            // Update username
            if (request.getUsername() != null) {
                manager.setUsername(request.getUsername());
            }

            // Update secteurName
            if (request.getSecteurName() != null) {
                if (!request.getSecteurName().equals(manager.getSecteurName()) &&
                        managerRepository.existsBySecteurName(request.getSecteurName())) {
                    throw new IllegalArgumentException("Secteur name already exists: " + request.getSecteurName());
                }
                manager.setSecteurName(request.getSecteurName());
            }

            // Update other fields
            if (request.getPhone() != null) {
                manager.setPhone(request.getPhone());
            }
            if (request.getAddress() != null) {
                manager.setAddress(request.getAddress());
            }
            if (request.getIsActive() != null) {
                manager.setIsActive(request.getIsActive());
                keycloakAdminService.setUserEnabled(manager.getKeycloakUserId(), request.getIsActive());
            }
            if (request.getAssignedAdminCode() != null) {
                Admin admin = adminRepository.findByCode(request.getAssignedAdminCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + request.getAssignedAdminCode()));
                manager.setAssignedAdmin(admin);
            }

            // Update custom attributes in Keycloak
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("code", List.of(manager.getCode()));
            attributes.put("secteurName", List.of(manager.getSecteurName()));
            attributes.put("phone", List.of(manager.getPhone()));
            attributes.put("address", List.of(manager.getAddress()));
            keycloakAdminService.updateUserAttributes(manager.getKeycloakUserId(), attributes);

            manager.setSyncedAt(LocalDateTime.now());
            return managerRepository.save(manager);

        } catch (Exception e) {
            log.error("Error updating manager: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update manager: " + e.getMessage(), e);
        }
    }

    @Override
    public Manager partialUpdateManager(String code, Map<String, Object> updates) {
        Manager manager = getManagerByCode(code);

        try {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "username":
                        manager.setUsername(value.toString());
                        break;
                    case "password":
                        keycloakAdminService.resetPassword(manager.getKeycloakUserId(), value.toString());
                        break;
                    case "secteurName":
                        String newSecteur = value.toString();
                        if (!newSecteur.equals(manager.getSecteurName()) &&
                                managerRepository.existsBySecteurName(newSecteur)) {
                            throw new IllegalArgumentException("Secteur name already exists: " + newSecteur);
                        }
                        manager.setSecteurName(newSecteur);
                        break;
                    case "phone":
                        manager.setPhone(value.toString());
                        break;
                    case "address":
                        manager.setAddress(value.toString());
                        break;
                    case "isActive":
                        Boolean isActive = Boolean.valueOf(value.toString());
                        manager.setIsActive(isActive);
                        keycloakAdminService.setUserEnabled(manager.getKeycloakUserId(), isActive);
                        break;
                    case "assignedAdminCode":
                        Admin admin = adminRepository.findByCode(value.toString())
                                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + value));
                        manager.setAssignedAdmin(admin);
                        break;
                }
            });

            manager.setSyncedAt(LocalDateTime.now());
            return managerRepository.save(manager);

        } catch (Exception e) {
            log.error("Error partially updating manager: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update manager: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteManager(String code) {
        Manager manager = getManagerByCode(code);

        try {
            // Delete from Keycloak first
            keycloakAdminService.deleteUser(manager.getKeycloakUserId());

            // Then delete from local database
            managerRepository.deleteByCode(code);

            log.info("Manager deleted successfully: {}", manager.getUsername());
        } catch (Exception e) {
            log.error("Error deleting manager: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete manager: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Driver> getManagerDrivers(String code) {
        if (!managerRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Manager not found: " + code);
        }
        return driverRepository.findByAssignedManager_Code(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getManagerClients(String code) {
        if (!managerRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Manager not found: " + code);
        }
        return clientRepository.findBySecteur_Code(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bordereau> getManagerBordereaux(String code) {
        if (!managerRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Manager not found: " + code);
        }
        return bordereauxRepository.findBySecteur_Code(code);
    }
}
