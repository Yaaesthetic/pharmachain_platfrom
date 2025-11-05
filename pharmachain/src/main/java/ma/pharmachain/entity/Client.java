package ma.pharmachain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client {
    @Id
    private String clientCode; // 400002
    private String name;
    private String address;
    private String phone;
    private String coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    private Manager secteur;

    private Boolean autoCreated;
}