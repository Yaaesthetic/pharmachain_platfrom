package ma.pharmachain.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.pharmachain.enums.BordereauStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Bordereau {
    @Id
    private String bordereauNumber; // 500001

    private LocalDate deliveryDate; // 220824

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver currentDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    private Manager secteur;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver originalDriver;

    @Enumerated(EnumType.STRING)
    private BordereauStatus status;

    @OneToMany(mappedBy = "bordereau", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryItem> deliveryItems = new ArrayList<>();

    private LocalDateTime scannedAt;
    private LocalDateTime completedAt;

    private Boolean autoCreated;
}