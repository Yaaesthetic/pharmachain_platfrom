package ma.pharmachain.controller;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.*;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.service.BordereauService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/bordereaux")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class BordereauController {

    private final BordereauService bordereauxService;

    @GetMapping
    public Page<Bordereau> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bordereauxService.listBordereaux(page, size);
    }

    @GetMapping("/{bordereauNumber}")
    public ResponseEntity<Bordereau> getOne(@PathVariable String bordereauNumber) {
        return ResponseEntity.ok(bordereauxService.getBordereauByNumber(bordereauNumber));
    }

    @PostMapping("/scan")
    public ResponseEntity<Bordereau> scan(@RequestBody BordereauScanRequest request) {
        return ResponseEntity.ok(bordereauxService.scanBordereau(request));
    }

    @PutMapping("/{bordereauNumber}")
    public ResponseEntity<Bordereau> update(
            @PathVariable String bordereauNumber,
            @RequestBody BordereauUpdateRequest request
    ) {
        return ResponseEntity.ok(bordereauxService.updateBordereau(bordereauNumber, request));
    }

    @PatchMapping("/{bordereauNumber}")
    public ResponseEntity<Bordereau> partialUpdate(
            @PathVariable String bordereauNumber,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(bordereauxService.partialUpdateBordereau(bordereauNumber, updates));
    }

    @DeleteMapping("/{bordereauNumber}")
    public ResponseEntity<Void> delete(@PathVariable String bordereauNumber) {
        bordereauxService.deleteBordereau(bordereauNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{bordereauNumber}/assignments")
    public ResponseEntity<Bordereau> reassign(
            @PathVariable String bordereauNumber,
            @RequestBody AssignmentRequest request
    ) {
        return ResponseEntity.ok(bordereauxService.reassignBordereau(
                bordereauNumber,
                request.getDriverCode(),
                request.getManagerCode()
        ));
    }

    @GetMapping("/{bordereauNumber}/delivery-items")
    public ResponseEntity<List<DeliveryItem>> getDeliveryItems(@PathVariable String bordereauNumber) {
        return ResponseEntity.ok(bordereauxService.getDeliveryItems(bordereauNumber));
    }
}
