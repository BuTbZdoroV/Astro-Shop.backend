package org.productservice.model.dto.specification.offer;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.productservice.model.entity.Offer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static Specification<Offer> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId != null ? criteriaBuilder.equal(root.get("userId"), userId) : null;
    }

    public static Specification<Offer> hasActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                active != null ? criteriaBuilder.equal(root.get("active"), active) : null;
    }

    public static Specification<Offer> hasAttributes(Map<String, Object> attributes) {
        return (root, query, criteriaBuilder) -> {
            if (attributes == null || attributes.isEmpty()) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                Expression<String> jsonPath = criteriaBuilder.function(
                        "jsonb_extract_path_text",
                        String.class,
                        root.get("attributes"),
                        criteriaBuilder.literal(key)
                );

                if (value != null) {
                    predicates.add(criteriaBuilder.equal(jsonPath, value.toString()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
