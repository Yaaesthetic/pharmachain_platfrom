package ma.pharmachain.dto;

import lombok.Data;

@Data
public class ManagerUpdateRequest {
    private String username;
    private String password;
    private String secteurName;
    private String phone;
    private String address;
    private Boolean isActive;
    private String assignedAdminCode;

    private String email;
    private String firstName;
    private String lastName;
}
