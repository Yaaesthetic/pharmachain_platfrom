package ma.pharmachain.service.impl;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.ClientCreateRequest;
import ma.pharmachain.exception.ResourceNotFoundException;
import ma.pharmachain.dto.ClientUpdateRequest;
import ma.pharmachain.entity.Client;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.entity.Manager;
import ma.pharmachain.repository.ClientRepository;
import ma.pharmachain.repository.DeliveryItemRepository;
import ma.pharmachain.repository.ManagerRepository;
import ma.pharmachain.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ManagerRepository managerRepository;
    private final DeliveryItemRepository deliveryItemRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Client> listClients(int page, int size) {
        return clientRepository.findAll(
                PageRequest.of(page, size, Sort.by("name").ascending())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientByCode(String clientCode) {
        return clientRepository.findByClientCode(clientCode)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientCode));
    }

    @Override
    public Client createClient(ClientCreateRequest request) {
        // Validate unique constraint
        if (clientRepository.existsByClientCode(request.getClientCode())) {
            throw new IllegalArgumentException("Client code already exists: " + request.getClientCode());
        }

        // Validate secteur is required
        if (request.getSecteurCode() == null || request.getSecteurCode().isEmpty()) {
            throw new IllegalArgumentException("Secteur (Manager) is required");
        }

        // Find secteur (Manager)
        Manager secteur = managerRepository.findByCode(request.getSecteurCode())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getSecteurCode()));

        // Create client
        Client client = new Client();
        client.setClientCode(request.getClientCode());
        client.setName(request.getName());
        client.setAddress(request.getAddress());
        client.setPhone(request.getPhone());
        client.setCoordinates(request.getCoordinates());
        client.setSecteur(secteur);
        client.setAutoCreated(false); // Manually created

        return clientRepository.save(client);
    }

    @Override
    public Client updateClient(String clientCode, ClientUpdateRequest request) {
        Client client = getClientByCode(clientCode);

        // Update mutable fields
        if (request.getName() != null) {
            client.setName(request.getName());
        }
        if (request.getAddress() != null) {
            client.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            client.setPhone(request.getPhone());
        }
        if (request.getCoordinates() != null) {
            client.setCoordinates(request.getCoordinates());
        }
        if (request.getSecteurCode() != null) {
            Manager secteur = managerRepository.findByCode(request.getSecteurCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getSecteurCode()));
            client.setSecteur(secteur);
        }

        return clientRepository.save(client);
    }

    @Override
    public Client partialUpdateClient(String clientCode, Map<String, Object> updates) {
        Client client = getClientByCode(clientCode);

        // Apply partial updates
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    client.setName(value.toString());
                    break;
                case "address":
                    client.setAddress(value.toString());
                    break;
                case "phone":
                    client.setPhone(value.toString());
                    break;
                case "coordinates":
                    client.setCoordinates(value.toString());
                    break;
                case "secteurCode":
                    Manager secteur = managerRepository.findByCode(value.toString())
                            .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + value));
                    client.setSecteur(secteur);
                    break;
            }
        });

        return clientRepository.save(client);
    }

    @Override
    public void deleteClient(String clientCode) {
        if (!clientRepository.existsByClientCode(clientCode)) {
            throw new ResourceNotFoundException("Client not found: " + clientCode);
        }
        clientRepository.deleteByClientCode(clientCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryItem> getClientDeliveryItems(String clientCode) {
        if (!clientRepository.existsByClientCode(clientCode)) {
            throw new ResourceNotFoundException("Client not found: " + clientCode);
        }
        return deliveryItemRepository.findByClient_ClientCode(clientCode);
    }
}

