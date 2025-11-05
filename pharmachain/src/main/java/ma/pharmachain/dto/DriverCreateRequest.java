package ma.pharmachain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DriverCreateRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String phone;

    @NotBlank(message = "AssignedManager is required")
    private String assignedManagerCode; // Required

    private String email;
    private String firstName;
    private String lastName;
}