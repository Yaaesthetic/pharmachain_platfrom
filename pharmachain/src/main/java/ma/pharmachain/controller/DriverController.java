package ma.pharmachain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.DriverCreateRequest;
import ma.pharmachain.dto.DriverUpdateRequest;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.entity.Driver;
import ma.pharmachain.service.DriverService;
import ma.pharmachain.service.AuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DRIVER')")
public class DriverController {

    private final DriverService driverService;
    private final AuthenticationService authenticationService;

    /**
     * Get authenticated driver's information from JWT token
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyInfo(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("keycloakUserId", jwt.getSubject());
        userInfo.put("username", jwt.getClaim("preferred_username"));
        userInfo.put("email", shouldExposeSensitiveEmail(authentication) ? jwt.getClaim("email") : "********");
        userInfo.put("firstName", jwt.getClaim("given_name"));
        userInfo.put("lastName", jwt.getClaim("family_name"));
        userInfo.put("code", jwt.getClaim("code"));
        userInfo.put("licenseNumber", jwt.getClaim("licenseNumber"));
        userInfo.put("phone", jwt.getClaim("phone"));
        userInfo.put("roles", authentication.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }

    private boolean shouldExposeSensitiveEmail(JwtAuthenticationToken authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Get authenticated driver's full profile from database
     */
    @GetMapping("/me/profile")
    public ResponseEntity<Driver> getMyProfile() {
        String keycloakUserId = authenticationService.getCurrentKeycloakUserId();
        Driver driver = driverService.getDriverByKeycloakUserId(keycloakUserId);
        return ResponseEntity.ok(driver);
    }

    /**
     * Get my bordereaux (driver's own deliveries)
     */
    @GetMapping("/me/bordereaux")
    public ResponseEntity<List<Bordereau>> getMyBordereaux() {
        String code = authenticationService.getCurrentUserCode();
        return ResponseEntity.ok(driverService.getDriverBordereaux(code));
    }

    /**
     * Get my delivery items
     */
    @GetMapping("/me/delivery-items")
    public ResponseEntity<List<DeliveryItem>> getMyDeliveryItems() {
        String code = authenticationService.getCurrentUserCode();
        return ResponseEntity.ok(driverService.getDriverDeliveryItems(code));
    }

    /**
     * List all drivers - Accessible by ADMIN and MANAGER
     */
    @GetMapping
    public ResponseEntity<Page<Driver>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(driverService.listDrivers(page, size));
    }

    /**
     * Get a specific driver by code
     */
    @GetMapping("/{code}")
    public ResponseEntity<Driver> getOne(@PathVariable String code) {
        return ResponseEntity.ok(driverService.getDriverByCode(code));
    }

    /**
     * Create a new driver - Only ADMIN and MANAGER
     */
    @PostMapping
    public ResponseEntity<Driver> create(@Valid @RequestBody DriverCreateRequest request) {
        Driver driver = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    /**
     * Update a driver - ADMIN and MANAGER can update, DRIVER can only update themselves
     */
    @PutMapping("/{code}")
    public ResponseEntity<Driver> update(
            @PathVariable String code,
            @RequestBody DriverUpdateRequest request
    ) {
        return ResponseEntity.ok(driverService.updateDriver(code, request));
    }

    /**
     * Partially update a driver
     */
    @PatchMapping("/{code}")
    public ResponseEntity<Driver> partialUpdate(
            @PathVariable String code,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(driverService.partialUpdateDriver(code, updates));
    }

    /**
     * Delete a driver - Only ADMIN and MANAGER
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        driverService.deleteDriver(code);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get bordereaux for a specific driver
     */
    @GetMapping("/{code}/bordereaux")
    public ResponseEntity<List<Bordereau>> getBordereaux(@PathVariable String code) {
        return ResponseEntity.ok(driverService.getDriverBordereaux(code));
    }

    /**
     * Get delivery items for a specific driver
     */
    @GetMapping("/{code}/delivery-items")
    public ResponseEntity<List<DeliveryItem>> getDeliveryItems(@PathVariable String code) {
        return ResponseEntity.ok(driverService.getDriverDeliveryItems(code));
    }
}
