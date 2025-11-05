package ma.pharmachain.repository;

import ma.pharmachain.entity.Admin;
import ma.pharmachain.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByKeycloakUserId(String keycloakUserId);

    // GET /managers - list all with pagination
    Page<Manager> findAll(Pageable pageable);

    // GET /managers/{code} - single lookup
    Optional<Manager> findByCode(String code);

    // Existence check
    boolean existsByCode(String code);
    boolean existsBySecteurName(String secteurName);

    // DELETE /managers/{code}
    void deleteByCode(String code);
}
