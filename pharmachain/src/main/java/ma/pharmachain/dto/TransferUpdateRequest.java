package ma.pharmachain.dto;

import lombok.Data;

@Data
public class TransferUpdateRequest {
    private String reason;
    private String transferBarcode;
}
