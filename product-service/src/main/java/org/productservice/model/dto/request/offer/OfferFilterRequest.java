package org.productservice.model.dto.request.offer;

import lombok.Data;

@Data
public class OfferFilterRequest {
    String name;
    String description;
    Long lotId;
    Long productId;
    String productName;
}
