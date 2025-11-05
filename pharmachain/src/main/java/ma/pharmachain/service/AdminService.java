package ma.pharmachain.service;

import ma.pharmachain.dto.AdminCreateRequest;
import ma.pharmachain.dto.AdminUpdateRequest;
import ma.pharmachain.entity.Admin;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface AdminService {

    Admin getAdminByKeycloakUserId(String keycloakUserId);

    Page<Admin> listAdmins(int page, int size);

    Admin getAdminById(Long id);

    Admin createAdmin(AdminCreateRequest request);

    Admin updateAdmin(Long id, AdminUpdateRequest request);

    Admin partialUpdateAdmin(Long id, Map<String, Object> updates);

    void deleteAdmin(Long id);
}

