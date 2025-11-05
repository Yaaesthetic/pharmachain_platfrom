package ma.pharmachain.repository;

import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.BordereauTransfer;
import ma.pharmachain.entity.Driver;
import ma.pharmachain.enums.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BordereauTransferRepository extends JpaRepository<BordereauTransfer, Long> {
    List<BordereauTransfer> findByBordereau(Bordereau bordereau);
    List<BordereauTransfer> findByFromDriver(Driver fromDriver);
    List<BordereauTransfer> findByToDriver(Driver toDriver);
    List<BordereauTransfer> findByStatus(TransferStatus status);
    Optional<BordereauTransfer> findByTransferBarcode(String transferBarcode);
    List<BordereauTransfer> findByFromDriver_Code(String fromDriverCode);
    List<BordereauTransfer> findByToDriver_Code(String toDriverCode);

    // GET /transfers - list all with pagination
    Page<BordereauTransfer> findAll(Pageable pageable);

    // GET /transfers/{id} - single lookup
    Optional<BordereauTransfer> findById(Long id);

    // Existence check
    boolean existsById(Long id);
}
