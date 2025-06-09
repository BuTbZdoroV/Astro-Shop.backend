package org.productservice.model.dto.response.lot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.productservice.model.dto.response.offer.OfferResponse;
import org.productservice.model.dto.response.product.ProductResponse;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotResponse {
    Long id;
    String name;
    ProductResponse product;
    List<OfferResponse> offers;
    Map<String, Object> attributes;
}
