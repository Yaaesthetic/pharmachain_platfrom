package ma.pharmachain.dto;

import lombok.Data;
import ma.pharmachain.enums.DeliveryItemStatus;

@Data
public class DeliveryItemUpdateRequest {
    private Integer nombreColis;
    private Integer nombreSachets;
    private DeliveryItemStatus status;
    private String deliveryNotes;
    private String recipientSignature;
}