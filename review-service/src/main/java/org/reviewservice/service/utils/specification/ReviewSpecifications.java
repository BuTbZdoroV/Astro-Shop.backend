package org.reviewservice.service.utils.specification;

import org.reviewservice.model.entity.Review;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ReviewSpecifications {

    public Specification<Review> hasSellerId(Long sellerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("sellerId"), sellerId);
    }

}
