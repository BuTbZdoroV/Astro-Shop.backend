package org.paymentservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private Long buyerId;
    private Long sellerId;
    private Long offerId;

    private Double amount;
    private Double commission;

    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt   ;


    public enum TransactionStatus {
        PENDING, HOLD, PAID, DISPUTED, REFUNDED, AUTO_COMPLETED, CANCELLED;
    }
}


