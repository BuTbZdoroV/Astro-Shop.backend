package org.productservice.model.dto.response.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.productservice.model.dto.response.lot.LotResponse;


import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {
    Long id;
    String name;
    String shortDescription;
    String longDescription;
    Double price;
    Boolean active;
    Long userId;
    Date createdAt;
    Integer availability;
    String pictureUrl;
    Map<String, Object> attributes;
    LotResponse lot;
}