package org.reviewservice.model.dto.other;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerStats {
    Double avgRating;
    Long reviewsCount;
}
