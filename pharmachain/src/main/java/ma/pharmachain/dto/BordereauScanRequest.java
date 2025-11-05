package ma.pharmachain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BordereauScanRequest {
    private String bordereauNumber;
    private LocalDate deliveryDate;
    private String driverCode;
    private String managerCode;
    private List<DeliveryItemRequest> deliveryItems;
}
