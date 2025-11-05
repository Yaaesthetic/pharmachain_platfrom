package ma.pharmachain.service.impl;

import lombok.RequiredArgsConstructor;
import ma.pharmachain.dto.*;
import ma.pharmachain.entity.*;
import ma.pharmachain.enums.*;
import ma.pharmachain.exception.ResourceNotFoundException;
import ma.pharmachain.repository.*;
import ma.pharmachain.service.BordereauService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BordereauServiceImpl implements BordereauService {

    private final BordereauRepository bordereauxRepository;
    private final DeliveryItemRepository deliveryItemRepository;
    private final DriverRepository driverRepository;
    private final ManagerRepository managerRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Bordereau> listBordereaux(int page, int size) {
        return bordereauxRepository.findAll(
                PageRequest.of(page, size, Sort.by("deliveryDate").descending())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Bordereau getBordereauByNumber(String bordereauNumber) {
        return bordereauxRepository.findByBordereauNumber(bordereauNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bordereau not found: " + bordereauNumber));
    }

    @Override
    public Bordereau scanBordereau(BordereauScanRequest request) {
        // Find or create bordereau
        Bordereau bordereau = bordereauxRepository.findByBordereauNumber(request.getBordereauNumber())
                .orElseGet(() -> {
                    Bordereau newBordereau = new Bordereau();
                    newBordereau.setBordereauNumber(request.getBordereauNumber());
                    newBordereau.setAutoCreated(true);
                    newBordereau.setScannedAt(LocalDateTime.now());
                    newBordereau.setStatus(BordereauStatus.CREATED);
                    return newBordereau;
                });

        // Update fields
        bordereau.setDeliveryDate(request.getDeliveryDate());

        // Auto-create/link Driver
        if (request.getDriverCode() != null) {
            Driver driver = driverRepository.findByCode(request.getDriverCode())
                    .orElseGet(() -> {
                        Driver newDriver = new Driver();
                        newDriver.setCode(request.getDriverCode());
                        newDriver.setUsername("driver_" + request.getDriverCode());
                        newDriver.setIsActive(true);
                        newDriver.setCreatedAt(LocalDateTime.now());
                        return driverRepository.save(newDriver);
                    });
            bordereau.setCurrentDriver(driver);
            bordereau.setOriginalDriver(driver);
        }

        // Auto-create/link Manager
        if (request.getManagerCode() != null) {
            Manager manager = managerRepository.findByCode(request.getManagerCode())
                    .orElseGet(() -> {
                        Manager newManager = new Manager();
                        newManager.setCode(request.getManagerCode());
                        newManager.setUsername("manager_" + request.getManagerCode());
                        newManager.setIsActive(true);
                        newManager.setCreatedAt(LocalDateTime.now());
                        return managerRepository.save(newManager);
                    });
            bordereau.setSecteur(manager);
        }

        // Save bordereau first
        Bordereau savedBordereau = bordereauxRepository.save(bordereau);

        // Auto-create/link DeliveryItems
        if (request.getDeliveryItems() != null) {
            for (DeliveryItemRequest itemRequest : request.getDeliveryItems()) {
                DeliveryItem item = deliveryItemRepository.findByBlNumber(itemRequest.getBlNumber())
                        .orElseGet(() -> {
                            DeliveryItem newItem = new DeliveryItem();
                            newItem.setBlNumber(itemRequest.getBlNumber());
                            newItem.setStatus(DeliveryItemStatus.PENDING);
                            return newItem;
                        });

                item.setBordereau(savedBordereau);
                item.setNombreColis(itemRequest.getNombreColis());
                item.setNombreSachets(itemRequest.getNombreSachets());

                // Auto-create/link Client
                if (itemRequest.getClientCode() != null) {
                    Client client = clientRepository.findByClientCode(itemRequest.getClientCode())
                            .orElseGet(() -> {
                                Client newClient = new Client();
                                newClient.setClientCode(itemRequest.getClientCode());
                                newClient.setName(itemRequest.getClientName());
                                newClient.setAddress(itemRequest.getClientAddress());
                                newClient.setSecteur(savedBordereau.getSecteur());
                                newClient.setAutoCreated(true);
                                return clientRepository.save(newClient);
                            });
                    item.setClient(client);
                }

                deliveryItemRepository.save(item);
            }
        }

        return savedBordereau;
    }

    @Override
    public Bordereau updateBordereau(String bordereauNumber, BordereauUpdateRequest request) {
        Bordereau bordereau = getBordereauByNumber(bordereauNumber);

        // Update mutable fields
        if (request.getDeliveryDate() != null) {
            bordereau.setDeliveryDate(request.getDeliveryDate());
        }
        if (request.getStatus() != null) {
            bordereau.setStatus(request.getStatus());
        }

        return bordereauxRepository.save(bordereau);
    }

    @Override
    public Bordereau partialUpdateBordereau(String bordereauNumber, Map<String, Object> updates) {
        Bordereau bordereau = getBordereauByNumber(bordereauNumber);

        // Apply partial updates
        updates.forEach((key, value) -> {
            switch (key) {
                case "status":
                    bordereau.setStatus(BordereauStatus.valueOf(value.toString()));
                    break;
                case "deliveryDate":
                    bordereau.setDeliveryDate(LocalDate.parse(value.toString()));
                    break;
                // Add other fields as needed
            }
        });

        return bordereauxRepository.save(bordereau);
    }

    @Override
    public void deleteBordereau(String bordereauNumber) {
        if (!bordereauxRepository.existsByBordereauNumber(bordereauNumber)) {
            throw new ResourceNotFoundException("Bordereau not found: " + bordereauNumber);
        }
        bordereauxRepository.deleteByBordereauNumber(bordereauNumber);
    }

    @Override
    public Bordereau reassignBordereau(String bordereauNumber, String driverCode, String managerCode) {
        Bordereau bordereau = getBordereauByNumber(bordereauNumber);

        if (driverCode != null) {
            Driver driver = driverRepository.findByCode(driverCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverCode));
            bordereau.setCurrentDriver(driver);
        }

        if (managerCode != null) {
            Manager manager = managerRepository.findByCode(managerCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + managerCode));
            bordereau.setSecteur(manager);
        }

        return bordereauxRepository.save(bordereau);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryItem> getDeliveryItems(String bordereauNumber) {
        if (!bordereauxRepository.existsByBordereauNumber(bordereauNumber)) {
            throw new ResourceNotFoundException("Bordereau not found: " + bordereauNumber);
        }
        return deliveryItemRepository.findByBordereau_BordereauNumber(bordereauNumber);
    }
}
