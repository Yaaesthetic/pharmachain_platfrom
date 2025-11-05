package ma.pharmachain.repository;

import ma.pharmachain.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByKeycloakUserId(String keycloakUserId);

    // GET /admins - list all with pagination
    Page<Admin> findAll(Pageable pageable);

    // GET /admins/{id} - single lookup by numeric id
    Optional<Admin> findById(Long id);

    // Additional queries for filtering
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByCode(String code);

    boolean existsByUsername(String username);
    boolean existsByCode(String code);

}