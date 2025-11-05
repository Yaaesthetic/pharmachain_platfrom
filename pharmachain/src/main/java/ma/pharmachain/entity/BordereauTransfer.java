package ma.pharmachain.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.pharmachain.enums.TransferStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BordereauTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bordereau bordereau;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver fromDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver toDriver;

    private LocalDateTime transferredAt;
    private String transferBarcode;
    private String reason;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;
}