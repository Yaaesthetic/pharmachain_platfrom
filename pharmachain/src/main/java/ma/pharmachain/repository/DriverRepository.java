package ma.pharmachain.repository;

import ma.pharmachain.entity.Driver;
import ma.pharmachain.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByKeycloakUserId(String keycloakUserId);

    boolean existsByCode(String code);

    // GET /drivers - list all with pagination
    Page<Driver> findAll(Pageable pageable);

    // GET /drivers/{code} - single lookup
    Optional<Driver> findByCode(String code);

    // Existence check
    boolean existsByLicenseNumber(String licenseNumber);

    // DELETE /drivers/{code}
    void deleteByCode(String code);

    // GET /managers/{code}/drivers - list drivers for manager
    List<Driver> findByAssignedManager_Code(String managerCode);
}

