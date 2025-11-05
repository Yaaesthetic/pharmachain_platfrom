package ma.pharmachain.service;

import ma.pharmachain.dto.ClientUpdateRequest;
import ma.pharmachain.dto.ClientCreateRequest;
import ma.pharmachain.entity.Client;
import ma.pharmachain.entity.DeliveryItem;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ClientService {

    Page<Client> listClients(int page, int size);

    Client getClientByCode(String clientCode);

    Client createClient(ClientCreateRequest request);

    Client updateClient(String clientCode, ClientUpdateRequest request);

    Client partialUpdateClient(String clientCode, Map<String, Object> updates);

    void deleteClient(String clientCode);

    List<DeliveryItem> getClientDeliveryItems(String clientCode);
}
