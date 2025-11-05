package ma.pharmachain.service.impl;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.DeliveryItemUpdateRequest;
import ma.pharmachain.dto.ProofRequest;
import ma.pharmachain.exception.ResourceNotFoundException;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.enums.DeliveryItemStatus;
import ma.pharmachain.repository.BordereauRepository;
import ma.pharmachain.repository.ClientRepository;
import ma.pharmachain.repository.DeliveryItemRepository;
import ma.pharmachain.service.DeliveryItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryItemServiceImpl implements DeliveryItemService {

    private final DeliveryItemRepository deliveryItemRepository;
    private final ClientRepository clientRepository;
    private final BordereauRepository bordereauxRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryItem> listDeliveryItems(int page, int size) {
        return deliveryItemRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").descending())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryItem getDeliveryItemByBlNumber(String blNumber) {
        return deliveryItemRepository.findByBlNumber(blNumber)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryItem not found: " + blNumber));
    }

    @Override
    public DeliveryItem updateDeliveryItem(String blNumber, DeliveryItemUpdateRequest request) {
        DeliveryItem item = getDeliveryItemByBlNumber(blNumber);

        // Update mutable fields
        if (request.getNombreColis() != null) {
            item.setNombreColis(request.getNombreColis());
        }
        if (request.getNombreSachets() != null) {
            item.setNombreSachets(request.getNombreSachets());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());

            // Auto-update deliveredAt timestamp when status changes to DELIVERED
            if (request.getStatus() == DeliveryItemStatus.DELIVERED && item.getDeliveredAt() == null) {
                item.setDeliveredAt(LocalDateTime.now());
            }
        }
        if (request.getDeliveryNotes() != null) {
            item.setDeliveryNotes(request.getDeliveryNotes());
        }
        if (request.getRecipientSignature() != null) {
            item.setRecipientSignature(request.getRecipientSignature());
        }

        return deliveryItemRepository.save(item);
    }

    @Override
    public DeliveryItem partialUpdateDeliveryItem(String blNumber, Map<String, Object> updates) {
        DeliveryItem item = getDeliveryItemByBlNumber(blNumber);

        // Apply partial updates
        updates.forEach((key, value) -> {
            switch (key) {
                case "nombreColis":
                    item.setNombreColis(Integer.valueOf(value.toString()));
                    break;
                case "nombreSachets":
                    item.setNombreSachets(Integer.valueOf(value.toString()));
                    break;
                case "status":
                    DeliveryItemStatus status = DeliveryItemStatus.valueOf(value.toString());
                    item.setStatus(status);
                    if (status == DeliveryItemStatus.DELIVERED && item.getDeliveredAt() == null) {
                        item.setDeliveredAt(LocalDateTime.now());
                    }
                    break;
                case "deliveryNotes":
                    item.setDeliveryNotes(value.toString());
                    break;
                case "recipientSignature":
                    item.setRecipientSignature(value.toString());
                    break;
                case "deliveredAt":
                    item.setDeliveredAt(LocalDateTime.parse(value.toString()));
                    break;
            }
        });

        return deliveryItemRepository.save(item);
    }

    @Override
    public void deleteDeliveryItem(String blNumber) {
        if (!deliveryItemRepository.existsByBlNumber(blNumber)) {
            throw new ResourceNotFoundException("DeliveryItem not found: " + blNumber);
        }
        deliveryItemRepository.deleteByBlNumber(blNumber);
    }

    @Override
    public DeliveryItem updateProof(String blNumber, ProofRequest request) {
        DeliveryItem item = getDeliveryItemByBlNumber(blNumber);

        if (request.getDeliveryNotes() != null) {
            item.setDeliveryNotes(request.getDeliveryNotes());
        }
        if (request.getRecipientSignature() != null) {
            item.setRecipientSignature(request.getRecipientSignature());
        }

        // Automatically mark as delivered when proof is provided
        if (item.getStatus() != DeliveryItemStatus.DELIVERED) {
            item.setStatus(DeliveryItemStatus.DELIVERED);
            item.setDeliveredAt(LocalDateTime.now());
        }

        return deliveryItemRepository.save(item);
    }
}
