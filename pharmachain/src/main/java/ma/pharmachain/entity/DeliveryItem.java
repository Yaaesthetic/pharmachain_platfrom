package ma.pharmachain.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.pharmachain.enums.DeliveryItemStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeliveryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bordereau bordereau;

    @Column(unique = true)
    private String blNumber; // 300001

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    private Integer nombreColis;
    private Integer nombreSachets;

    @Enumerated(EnumType.STRING)
    private DeliveryItemStatus status;

    private LocalDateTime deliveredAt;
    private String deliveryNotes;
    private String recipientSignature;
}