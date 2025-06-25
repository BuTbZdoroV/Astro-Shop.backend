package org.reviewservice.service.utils;

import org.reviewservice.model.dto.response.ReviewResponse;
import org.reviewservice.model.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewUtils {

    public ReviewResponse buildResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .buyerId(review.getBuyerId())
                .sellerId(review.getSellerId())
                .offerId(review.getOfferId())
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
