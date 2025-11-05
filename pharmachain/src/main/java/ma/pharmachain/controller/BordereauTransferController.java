package ma.pharmachain.controller;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.TransferCreateRequest;
import ma.pharmachain.dto.TransferStatusRequest;
import ma.pharmachain.dto.TransferUpdateRequest;
import ma.pharmachain.entity.BordereauTransfer;
import ma.pharmachain.service.BordereauTransferService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class BordereauTransferController {

    private final BordereauTransferService transferService;

    // POST /bordereaux/{bordereauNumber}/transfers
    @PostMapping("/bordereaux/{bordereauNumber}/transfers")
    public ResponseEntity<BordereauTransfer> createTransfer(
            @PathVariable String bordereauNumber,
            @RequestBody TransferCreateRequest request
    ) {
        BordereauTransfer transfer = transferService.createTransfer(bordereauNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }

    // GET /transfers - list with pagination
    @GetMapping("/transfers")
    public Page<BordereauTransfer> listTransfers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return transferService.listTransfers(page, size);
    }

    // GET /transfers/{id}
    @GetMapping("/transfers/{id}")
    public ResponseEntity<BordereauTransfer> getTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    // PUT /transfers/{id}
    @PutMapping("/transfers/{id}")
    public ResponseEntity<BordereauTransfer> updateTransfer(
            @PathVariable Long id,
            @RequestBody TransferUpdateRequest request
    ) {
        return ResponseEntity.ok(transferService.updateTransfer(id, request));
    }

    // PATCH /transfers/{id}/status
    @PatchMapping("/transfers/{id}/status")
    public ResponseEntity<BordereauTransfer> updateStatus(
            @PathVariable Long id,
            @RequestBody TransferStatusRequest request
    ) {
        return ResponseEntity.ok(transferService.updateTransferStatus(id, request.getStatus()));
    }

    // DELETE /transfers/{id}
    @DeleteMapping("/transfers/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }
}
