package ma.pharmachain.service;

import ma.pharmachain.dto.DriverCreateRequest;
import ma.pharmachain.dto.DriverUpdateRequest;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.DeliveryItem;
import ma.pharmachain.entity.Driver;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DriverService {

    Driver getDriverByKeycloakUserId(String keycloakUserId);

    Page<Driver> listDrivers(int page, int size);

    Driver getDriverByCode(String code);

    Driver createDriver(DriverCreateRequest request);

    Driver updateDriver(String code, DriverUpdateRequest request);

    Driver partialUpdateDriver(String code, Map<String, Object> updates);

    void deleteDriver(String code);

    List<Bordereau> getDriverBordereaux(String code);

    List<DeliveryItem> getDriverDeliveryItems(String code);
}

