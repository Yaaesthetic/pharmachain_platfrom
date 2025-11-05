package ma.pharmachain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.pharmachain.dto.DriverCreateRequest;
import ma.pharmachain.dto.DriverUpdateRequest;
import ma.pharmachain.exception.ResourceNotFoundException;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.entity.Driver;
import ma.pharmachain.entity.Manager;
import ma.pharmachain.repository.BordereauRepository;
import ma.pharmachain.repository.DeliveryItemRepository;
import ma.pharmachain.repository.DriverRepository;
import ma.pharmachain.repository.ManagerRepository;
import ma.pharmachain.service.DriverService;
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
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final ManagerRepository managerRepository;
    private final BordereauRepository bordereauxRepository;
    private final DeliveryItemRepository deliveryItemRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Override
    @Transactional(readOnly = true)
    public Driver getDriverByKeycloakUserId(String keycloakUserId) {
        return driverRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with Keycloak ID: " + keycloakUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Driver> listDrivers(int page, int size) {
        return driverRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Driver getDriverByCode(String code) {
        return driverRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + code));
    }

    @Override
    public Driver createDriver(DriverCreateRequest request) {
        // Validate unique constraints
        if (driverRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Driver code already exists: " + request.getCode());
        }
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new IllegalArgumentException("License number already exists: " + request.getLicenseNumber());
        }

        // Validate assignedManager is required
        if (request.getAssignedManagerCode() == null || request.getAssignedManagerCode().isEmpty()) {
            throw new IllegalArgumentException("AssignedManager is required");
        }

        // Find assignedManager
        Manager manager = managerRepository.findByCode(request.getAssignedManagerCode())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getAssignedManagerCode()));

        try {
            // Step 1: Create user in Keycloak
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("code", List.of(request.getCode()));
            attributes.put("userType", List.of("DRIVER"));
            attributes.put("licenseNumber", List.of(request.getLicenseNumber()));
            attributes.put("phone", List.of(request.getPhone()));

            String keycloakUserId = keycloakAdminService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    attributes
            );

            // Step 2: Assign DRIVER role in Keycloak
            keycloakAdminService.assignRoleToUser(keycloakUserId, "DRIVER");

            // Step 3: Create driver in local database
            Driver driver = new Driver();
            driver.setKeycloakUserId(keycloakUserId);
            driver.setCode(request.getCode());
            driver.setUsername(request.getUsername());
            driver.setLicenseNumber(request.getLicenseNumber());
            driver.setPhone(request.getPhone());
            driver.setAssignedManager(manager);
            driver.setIsActive(true);
            driver.setCreatedAt(LocalDateTime.now());
            driver.setSyncedAt(LocalDateTime.now());

            Driver savedDriver = driverRepository.save(driver);
            log.info("Driver created successfully: {} with Keycloak ID: {}",
                    request.getUsername(), keycloakUserId);

            return savedDriver;

        } catch (Exception e) {
            log.error("Error creating driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create driver: " + e.getMessage(), e);
        }
    }

    @Override
    public Driver updateDriver(String code, DriverUpdateRequest request) {
        Driver driver = getDriverByCode(code);

        try {
            // Update in Keycloak if needed
            if (request.getEmail() != null || request.getFirstName() != null ||
                    request.getLastName() != null) {
                keycloakAdminService.updateUser(
                        driver.getKeycloakUserId(),
                        request.getEmail(),
                        request.getFirstName(),
                        request.getLastName()
                );
            }

            // Update password if provided
            if (request.getPassword() != null) {
                keycloakAdminService.resetPassword(driver.getKeycloakUserId(), request.getPassword());
            }

            // Update fields
            if (request.getUsername() != null) {
                driver.setUsername(request.getUsername());
            }
            if (request.getLicenseNumber() != null) {
                if (!request.getLicenseNumber().equals(driver.getLicenseNumber()) &&
                        driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                    throw new IllegalArgumentException("License number already exists: " + request.getLicenseNumber());
                }
                driver.setLicenseNumber(request.getLicenseNumber());
            }
            if (request.getPhone() != null) {
                driver.setPhone(request.getPhone());
            }
            if (request.getIsActive() != null) {
                driver.setIsActive(request.getIsActive());
                keycloakAdminService.setUserEnabled(driver.getKeycloakUserId(), request.getIsActive());
            }
            if (request.getAssignedManagerCode() != null) {
                Manager manager = managerRepository.findByCode(request.getAssignedManagerCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getAssignedManagerCode()));
                driver.setAssignedManager(manager);
            }

            // Update custom attributes in Keycloak
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("code", List.of(driver.getCode()));
            attributes.put("licenseNumber", List.of(driver.getLicenseNumber()));
            attributes.put("phone", List.of(driver.getPhone()));
            keycloakAdminService.updateUserAttributes(driver.getKeycloakUserId(), attributes);

            driver.setSyncedAt(LocalDateTime.now());
            return driverRepository.save(driver);

        } catch (Exception e) {
            log.error("Error updating driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update driver: " + e.getMessage(), e);
        }
    }

    @Override
    public Driver partialUpdateDriver(String code, Map<String, Object> updates) {
        Driver driver = getDriverByCode(code);

        try {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "username":
                        driver.setUsername(value.toString());
                        break;
                    case "password":
                        keycloakAdminService.resetPassword(driver.getKeycloakUserId(), value.toString());
                        break;
                    case "licenseNumber":
                        String newLicense = value.toString();
                        if (!newLicense.equals(driver.getLicenseNumber()) &&
                                driverRepository.existsByLicenseNumber(newLicense)) {
                            throw new IllegalArgumentException("License number already exists: " + newLicense);
                        }
                        driver.setLicenseNumber(newLicense);
                        break;
                    case "phone":
                        driver.setPhone(value.toString());
                        break;
                    case "isActive":
                        Boolean isActive = Boolean.valueOf(value.toString());
                        driver.setIsActive(isActive);
                        keycloakAdminService.setUserEnabled(driver.getKeycloakUserId(), isActive);
                        break;
                    case "assignedManagerCode":
                        Manager manager = managerRepository.findByCode(value.toString())
                                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + value));
                        driver.setAssignedManager(manager);
                        break;
                }
            });

            driver.setSyncedAt(LocalDateTime.now());
            return driverRepository.save(driver);

        } catch (Exception e) {
            log.error("Error partially updating driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update driver: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDriver(String code) {
        Driver driver = getDriverByCode(code);

        try {
            // Delete from Keycloak first
            keycloakAdminService.deleteUser(driver.getKeycloakUserId());

            // Then delete from local database
            driverRepository.deleteByCode(code);

            log.info("Driver deleted successfully: {}", driver.getUsername());
        } catch (Exception e) {
            log.error("Error deleting driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete driver: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bordereau> getDriverBordereaux(String code) {
        if (!driverRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Driver not found: " + code);
        }
        return bordereauxRepository.findByCurrentDriver_Code(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryItem> getDriverDeliveryItems(String code) {
        if (!driverRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("Driver not found: " + code);
        }
        return deliveryItemRepository.findByBordereau_CurrentDriver_Code(code);
    }
}
