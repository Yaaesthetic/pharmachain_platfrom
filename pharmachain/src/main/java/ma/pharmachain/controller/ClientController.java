package ma.pharmachain.controller;
import ma.pharmachain.dto.ClientUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.ClientCreateRequest;
import ma.pharmachain.entity.Client;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ClientController {

    private final ClientService clientService;

    // GET /clients - list with pagination
    @GetMapping
    public Page<Client> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return clientService.listClients(page, size);
    }

    // GET /clients/{clientCode}
    @GetMapping("/{clientCode}")
    public ResponseEntity<Client> getOne(@PathVariable String clientCode) {
        return ResponseEntity.ok(clientService.getClientByCode(clientCode));
    }

    // POST /clients
    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody ClientCreateRequest request) {
        Client client = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    // PUT /clients/{clientCode}
    @PutMapping("/{clientCode}")
    public ResponseEntity<Client> update(
            @PathVariable String clientCode,
            @RequestBody ClientUpdateRequest request
    ) {
        return ResponseEntity.ok(clientService.updateClient(clientCode, request));
    }

    // PATCH /clients/{clientCode}
    @PatchMapping("/{clientCode}")
    public ResponseEntity<Client> partialUpdate(
            @PathVariable String clientCode,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(clientService.partialUpdateClient(clientCode, updates));
    }

    // DELETE /clients/{clientCode}
    @DeleteMapping("/{clientCode}")
    public ResponseEntity<Void> delete(@PathVariable String clientCode) {
        clientService.deleteClient(clientCode);
        return ResponseEntity.noContent().build();
    }

    // GET /clients/{clientCode}/delivery-items
    @GetMapping("/{clientCode}/delivery-items")
    public ResponseEntity<List<DeliveryItem>> getDeliveryItems(@PathVariable String clientCode) {
        return ResponseEntity.ok(clientService.getClientDeliveryItems(clientCode));
    }
}

