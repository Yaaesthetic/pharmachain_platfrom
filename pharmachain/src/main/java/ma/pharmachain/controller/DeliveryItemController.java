package ma.pharmachain.controller;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.DeliveryItemUpdateRequest;
import ma.pharmachain.dto.ProofRequest;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.service.DeliveryItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery-items")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DeliveryItemController {

    private final DeliveryItemService deliveryItemService;

    // GET /delivery-items - list with pagination
    @GetMapping
    public Page<DeliveryItem> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return deliveryItemService.listDeliveryItems(page, size);
    }

    // GET /delivery-items/{blNumber}
    @GetMapping("/{blNumber}")
    public ResponseEntity<DeliveryItem> getOne(@PathVariable String blNumber) {
        return ResponseEntity.ok(deliveryItemService.getDeliveryItemByBlNumber(blNumber));
    }

    // PUT /delivery-items/{blNumber}
    @PutMapping("/{blNumber}")
    public ResponseEntity<DeliveryItem> update(
            @PathVariable String blNumber,
            @RequestBody DeliveryItemUpdateRequest request
    ) {
        return ResponseEntity.ok(deliveryItemService.updateDeliveryItem(blNumber, request));
    }

    // PATCH /delivery-items/{blNumber}
    @PatchMapping("/{blNumber}")
    public ResponseEntity<DeliveryItem> partialUpdate(
            @PathVariable String blNumber,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(deliveryItemService.partialUpdateDeliveryItem(blNumber, updates));
    }

    // DELETE /delivery-items/{blNumber}
    @DeleteMapping("/{blNumber}")
    public ResponseEntity<Void> delete(@PathVariable String blNumber) {
        deliveryItemService.deleteDeliveryItem(blNumber);
        return ResponseEntity.noContent().build();
    }

    // PUT /delivery-items/{blNumber}/proof
    @PutMapping("/{blNumber}/proof")
    public ResponseEntity<DeliveryItem> updateProof(
            @PathVariable String blNumber,
            @RequestBody ProofRequest request
    ) {
        return ResponseEntity.ok(deliveryItemService.updateProof(blNumber, request));
    }
}
