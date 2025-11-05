package ma.pharmachain.dto;

import lombok.Data;
import ma.pharmachain.enums.BordereauStatus;

import java.time.LocalDate;

@Data
public class BordereauUpdateRequest {
    private LocalDate deliveryDate;
    private BordereauStatus status;
}
