package ma.pharmachain.service;

import ma.pharmachain.dto.ManagerCreateRequest;
import ma.pharmachain.dto.ManagerUpdateRequest;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.Client;
import ma.pharmachain.entity.Driver;
import ma.pharmachain.entity.Manager;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ManagerService {

    Manager getManagerByKeycloakUserId(String keycloakUserId);

    Page<Manager> listManagers(int page, int size);

    Manager getManagerByCode(String code);

    Manager createManager(ManagerCreateRequest request);

    Manager updateManager(String code, ManagerUpdateRequest request);

    Manager partialUpdateManager(String code, Map<String, Object> updates);

    void deleteManager(String code);

    List<Driver> getManagerDrivers(String code);

    List<Client> getManagerClients(String code);

    List<Bordereau> getManagerBordereaux(String code);
}
