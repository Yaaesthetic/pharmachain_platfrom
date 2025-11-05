package ma.pharmachain.service;

import ma.pharmachain.dto.BordereauScanRequest;
import ma.pharmachain.dto.BordereauUpdateRequest;
import ma.pharmachain.entity.Bordereau;
import ma.pharmachain.entity.DeliveryItem;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface BordereauService {

    Page<Bordereau> listBordereaux(int page, int size);

    Bordereau getBordereauByNumber(String bordereauNumber);

    Bordereau scanBordereau(BordereauScanRequest request);

    Bordereau updateBordereau(String bordereauNumber, BordereauUpdateRequest request);

    Bordereau partialUpdateBordereau(String bordereauNumber, Map<String, Object> updates);

    void deleteBordereau(String bordereauNumber);

    Bordereau reassignBordereau(String bordereauNumber, String driverCode, String managerCode);

    List<DeliveryItem> getDeliveryItems(String bordereauNumber);
}

