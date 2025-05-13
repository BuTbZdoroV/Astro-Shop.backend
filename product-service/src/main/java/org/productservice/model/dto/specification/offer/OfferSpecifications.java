package org.productservice.model.dto.specification.offer;

import org.productservice.model.entity.Offer;
import org.springframework.data.jpa.domain.Specification;

public class OfferSpecifications {

    public static Specification<Offer> hasId(Long id) {
        return (root, query, criteriaBuilder) ->
                id != null ? criteriaBuilder.equal(root.get("id"), id) : null;
    }

    public static Specification<Offer> nameLike(String name) {
        return (root, query, criteriaBuilder) ->
                name != null ? criteriaBuilder.like(root.get("name"), "%" + name + "%") : null;
    }

    public static Specification<Offer> hasLotId(Long lotId) {
        return (root, query, criteriaBuilder) ->
                lotId != null ? criteriaBuilder.equal(root.get("lot").get("id"), lotId) : null;
    }

}
