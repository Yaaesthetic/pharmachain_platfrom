package ma.pharmachain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keycloakUserId; // UUID from Keycloak

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String code; // e.g., 100001 for driver, 200001 for manager
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime syncedAt; // Last sync with Keycloak

}