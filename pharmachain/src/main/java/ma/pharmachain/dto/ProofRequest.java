package ma.pharmachain.dto;

import lombok.Data;

@Data
public class ProofRequest {
    private String deliveryNotes;
    private String recipientSignature;
}
