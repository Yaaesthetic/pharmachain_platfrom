package ma.pharmachain.service;

import ma.pharmachain.dto.DeliveryItemUpdateRequest;
import ma.pharmachain.dto.ProofRequest;
import ma.pharmachain.entity.DeliveryItem;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DeliveryItemService {

    Page<DeliveryItem> listDeliveryItems(int page, int size);

    DeliveryItem getDeliveryItemByBlNumber(String blNumber);

    DeliveryItem updateDeliveryItem(String blNumber, DeliveryItemUpdateRequest request);

    DeliveryItem partialUpdateDeliveryItem(String blNumber, Map<String, Object> updates);

    void deleteDeliveryItem(String blNumber);

    DeliveryItem updateProof(String blNumber, ProofRequest request);
}
