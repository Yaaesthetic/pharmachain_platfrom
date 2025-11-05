package ma.pharmachain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminCreateRequest {
    @NotBlank(message = "Code is required")
    private String code; // e.g., 300001

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String email;
    private String firstName;
    private String lastName;
}

