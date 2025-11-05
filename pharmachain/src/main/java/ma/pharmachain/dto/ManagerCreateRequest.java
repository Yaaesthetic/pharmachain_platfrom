package ma.pharmachain.dto;

import lombok.Data;

@Data
public class ManagerCreateRequest {
    private String code; // e.g., 200001
    private String username;
    private String password;
    private String secteurName;
    private String phone;
    private String address;
    private String assignedAdminCode; // Required

    private String email;
    private String firstName;
    private String lastName;
}

