package org.reviewservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews", indexes = @Index(name = "idx_seller_id", columnList = "sellerId"))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long sellerId;
    @Column(nullable = false)
    private Long buyerId;
    @Column(nullable = false)
    private Long offerId;
    @Size(min = 0, max = 10)
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;


}
