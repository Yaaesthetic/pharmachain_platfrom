package ma.pharmachain.repository;

import ma.pharmachain.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    // GET /managers/{code}/clients - list clients for manager
    List<Client> findBySecteur_Code(String managerCode);

    // GET /clients - list all with pagination
    Page<Client> findAll(Pageable pageable);

    // GET /clients/{clientCode} - single lookup
    Optional<Client> findByClientCode(String clientCode);

    // Existence check
    boolean existsByClientCode(String clientCode);

    // DELETE /clients/{clientCode}
    void deleteByClientCode(String clientCode);
}
