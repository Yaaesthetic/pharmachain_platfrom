package ma.pharmachain.controller;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.ManagerCreateRequest;
import ma.pharmachain.dto.ManagerUpdateRequest;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.Client;
import ma.pharmachain.entity.Driver;
import ma.pharmachain.entity.Manager;
import ma.pharmachain.service.ManagerService;
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
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@PreAuthorize("(hasRole('MANAGER'))")
public class ManagerController {

    private final ManagerService managerService;
    private final AuthenticationService authenticationService;

    /**
     * Get authenticated manager's information from JWT token
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
        userInfo.put("secteurName", jwt.getClaim("secteurName"));
        userInfo.put("phone", jwt.getClaim("phone"));
        userInfo.put("roles", authentication.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }

    private boolean shouldExposeSensitiveEmail(JwtAuthenticationToken authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Get authenticated manager's full profile from database
     */
    @GetMapping("/me/profile")
    public ResponseEntity<Manager> getMyProfile() {
        String keycloakUserId = authenticationService.getCurrentKeycloakUserId();
        Manager manager = managerService.getManagerByKeycloakUserId(keycloakUserId);
        return ResponseEntity.ok(manager);
    }

    /**
     * Get my drivers (drivers assigned to the authenticated manager)
     */
    @GetMapping("/me/drivers")
    public ResponseEntity<List<Driver>> getMyDrivers() {
        String code = authenticationService.getCurrentUserCode();
        return ResponseEntity.ok(managerService.getManagerDrivers(code));
    }

    /**
     * Get my clients (clients in my sector)
     */
    @GetMapping("/me/clients")
    public ResponseEntity<List<Client>> getMyClients() {
        String code = authenticationService.getCurrentUserCode();
        return ResponseEntity.ok(managerService.getManagerClients(code));
    }

    /**
     * Get my bordereaux
     */
    @GetMapping("/me/bordereaux")
    public ResponseEntity<List<Bordereau>> getMyBordereaux() {
        String code = authenticationService.getCurrentUserCode();
        return ResponseEntity.ok(managerService.getManagerBordereaux(code));
    }

    /**
     * List all managers - Accessible by ADMIN and MANAGER roles
     */
    @GetMapping
    public ResponseEntity<Page<Manager>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(managerService.listManagers(page, size));
    }

    /**
     * Get a specific manager by code
     */
    @GetMapping("/{code}")
    public ResponseEntity<Manager> getOne(@PathVariable String code) {
        return ResponseEntity.ok(managerService.getManagerByCode(code));
    }

    /**
     * Create a new manager - Only ADMIN can create managers
     */
    @PostMapping
    public ResponseEntity<Manager> create(@RequestBody ManagerCreateRequest request) {
        Manager manager = managerService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(manager);
    }

    /**
     * Update a manager - ADMIN can update any, MANAGER can only update themselves
     */
    @PutMapping("/{code}")
    public ResponseEntity<Manager> update(
            @PathVariable String code,
            @RequestBody ManagerUpdateRequest request
    ) {
        return ResponseEntity.ok(managerService.updateManager(code, request));
    }

    /**
     * Partially update a manager
     */
    @PatchMapping("/{code}")
    public ResponseEntity<Manager> partialUpdate(
            @PathVariable String code,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(managerService.partialUpdateManager(code, updates));
    }

    /**
     * Delete a manager - Only ADMIN
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        managerService.deleteManager(code);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get drivers for a specific manager - ADMIN or the manager themselves
     */
    @GetMapping("/{code}/drivers")
    public ResponseEntity<List<Driver>> getDrivers(@PathVariable String code) {
        return ResponseEntity.ok(managerService.getManagerDrivers(code));
    }

    /**
     * Get clients for a specific manager
     */
    @GetMapping("/{code}/clients")
    public ResponseEntity<List<Client>> getClients(@PathVariable String code) {
        return ResponseEntity.ok(managerService.getManagerClients(code));
    }

    /**
     * Get bordereaux for a specific manager
     */
    @GetMapping("/{code}/bordereaux")
    public ResponseEntity<List<Bordereau>> getBordereaux(@PathVariable String code) {
        return ResponseEntity.ok(managerService.getManagerBordereaux(code));
    }
}
