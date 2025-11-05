package ma.pharmachain.dto;

import lombok.Data;

@Data
public class DeliveryItemRequest {
    private String blNumber;
    private String clientCode;
    private String clientName;
    private String clientAddress;
    private Integer nombreColis;
    private Integer nombreSachets;
}
