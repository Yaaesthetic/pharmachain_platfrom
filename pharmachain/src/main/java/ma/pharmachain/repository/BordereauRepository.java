package ma.pharmachain.repository;

import ma.pharmachain.entity.*;
import ma.pharmachain.enums.BordereauStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BordereauRepository extends JpaRepository<Bordereau, String> {

    // GET /bordereaux - list all with pagination
    Page<Bordereau> findAll(Pageable pageable);

    // GET /bordereaux/{bordereauNumber} - single lookup
    Optional<Bordereau> findByBordereauNumber(String bordereauNumber);

    // POST /bordereaux/scan - existence check for upsert logic
    boolean existsByBordereauNumber(String bordereauNumber);

    // DELETE /bordereaux/{bordereauNumber}
    void deleteByBordereauNumber(String bordereauNumber);


    List<Bordereau> findByCurrentDriver_Code(String code);

    // GET /managers/{code}/bordereaux - list bordereaux for manager
    List<Bordereau> findBySecteur_Code(String managerCode);
}
