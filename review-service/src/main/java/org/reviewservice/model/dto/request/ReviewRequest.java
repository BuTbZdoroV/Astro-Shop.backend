package org.reviewservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    Long id;
    Long sellerId;
    Long buyerId;
    Long offerId;
    Integer rating;
    String comment;

}
