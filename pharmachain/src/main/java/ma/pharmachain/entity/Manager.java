package ma.pharmachain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@DiscriminatorValue("MANAGER")
@Getter
@Setter
@NoArgsConstructor
public class Manager extends User {
    private String secteurName;
    private String phone;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private Admin assignedAdmin;
}