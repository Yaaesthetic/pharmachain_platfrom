package ma.pharmachain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DRIVER")
@Getter
@Setter
@NoArgsConstructor
public class Driver extends User {
    @Column(unique = true)
    private String licenseNumber;
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    private Manager assignedManager;
}