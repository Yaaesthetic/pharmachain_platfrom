package ma.pharmachain.dto;

import lombok.Data;

@Data
public class DriverUpdateRequest {
    private String username;
    private String password;
    private String licenseNumber;
    private String phone;
    private Boolean isActive;
    private String assignedManagerCode;

    private String email;
    private String firstName;
    private String lastName;
}
