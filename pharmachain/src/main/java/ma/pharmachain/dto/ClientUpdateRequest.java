package ma.pharmachain.dto;

import lombok.Data;

@Data
public class ClientUpdateRequest {
    private String name;
    private String address;
    private String phone;
    private String coordinates;
    private String secteurCode;
}
