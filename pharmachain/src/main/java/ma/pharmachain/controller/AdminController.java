package ma.pharmachain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.AdminCreateRequest;
import ma.pharmachain.dto.AdminUpdateRequest;
import ma.pharmachain.entity.Admin;
import ma.pharmachain.service.AdminService;
import ma.pharmachain.service.AuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthenticationService authenticationService;

    /**
     * Get authenticated admin's information from JWT token
     * Accessible by any authenticated user
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyInfo(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("keycloakUserId", jwt.getSubject());
        userInfo.put("username", jwt.getClaim("preferred_username"));
        userInfo.put("email", jwt.getClaim("email"));
        userInfo.put("firstName", jwt.getClaim("given_name"));
        userInfo.put("lastName", jwt.getClaim("family_name"));
        userInfo.put("code", jwt.getClaim("code"));
        userInfo.put("roles", authentication.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Get authenticated admin's full profile from database
     */
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> getMyProfile() {
        String keycloakUserId = authenticationService.getCurrentKeycloakUserId();
        // You'll need to add this method to AdminService
        Admin admin = adminService.getAdminByKeycloakUserId(keycloakUserId);
        return ResponseEntity.ok(admin);
    }

    /**
     * List all admins - Only accessible by ADMIN role
     */
    // GET /admins - list with pagination
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Admin>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(adminService.listAdmins(page, size));
    }

    /**
     * Get a specific admin by ID - Only accessible by ADMIN role
     */
    // GET /admins/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    /**
     * Create a new admin - Only accessible by ADMIN role
     */
    // POST /admins
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> create(@Valid @RequestBody AdminCreateRequest request) {
        Admin admin = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }

    /**
     * Update an admin - Only accessible by ADMIN role
     */
    // PUT /admins/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> update(
            @PathVariable Long id,
            @RequestBody AdminUpdateRequest request
    ) {
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

    /**
     * Partially update an admin - Only accessible by ADMIN role
     */
    // PATCH /admins/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(adminService.partialUpdateAdmin(id, updates));
    }

    /**
     * Delete an admin - Only accessible by ADMIN role
     */
    // DELETE /admins/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
