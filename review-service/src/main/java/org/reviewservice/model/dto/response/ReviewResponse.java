package org.reviewservice.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    Long id;
    Long sellerId;
    Long buyerId;
    Long offerId;
    Integer rating;
    String comment;
    LocalDateTime createdAt;

}
