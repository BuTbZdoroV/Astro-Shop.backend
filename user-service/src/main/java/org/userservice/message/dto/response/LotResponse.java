package org.userservice.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
