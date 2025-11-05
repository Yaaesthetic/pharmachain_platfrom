package ma.pharmachain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientCreateRequest {
    @NotBlank(message = "Client code is required")
    private String clientCode; // e.g., 400002

    @NotBlank(message = "Name is required")
    private String name;

    private String address;
    private String phone;
    private String coordinates; // GPS coordinates

    @NotBlank(message = "Secteur (Manager) is required")
    private String secteurCode; // Required
}

