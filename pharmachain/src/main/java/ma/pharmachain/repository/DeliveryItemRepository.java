package ma.pharmachain.repository;

import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.Client;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.enums.DeliveryItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {

    // GET /bordereaux/{bordereauNumber}/delivery-items
    List<DeliveryItem> findByBordereau_BordereauNumber(String bordereauNumber);

    // POST /bordereaux/scan - existence check for BL numbers
    boolean existsByBlNumber(String blNumber);

    // GET /delivery-items - list all with pagination
    Page<DeliveryItem> findAll(Pageable pageable);

    // GET /delivery-items/{blNumber} - single lookup
    Optional<DeliveryItem> findByBlNumber(String blNumber);


    // DELETE /delivery-items/{blNumber}
    void deleteByBlNumber(String blNumber);


    // GET /drivers/{code}/delivery-items - list delivery items for driver
    List<DeliveryItem> findByBordereau_CurrentDriver_Code(String driverCode);

    // GET /clients/{clientCode}/delivery-items - list delivery items for client
    List<DeliveryItem> findByClient_ClientCode(String clientCode);
}
