package ma.pharmachain.dto;

import lombok.Data;

@Data
public class AdminUpdateRequest {
    private String code;
    private String username;
    private String password;
    private Boolean isActive;

    private String email;
    private String firstName;
    private String lastName;
}
