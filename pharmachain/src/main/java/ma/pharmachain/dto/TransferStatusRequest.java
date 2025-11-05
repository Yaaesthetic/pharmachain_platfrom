package ma.pharmachain.dto;

import lombok.Data;
import ma.pharmachain.enums.TransferStatus;

@Data
public class TransferStatusRequest {
    private TransferStatus status;
}
