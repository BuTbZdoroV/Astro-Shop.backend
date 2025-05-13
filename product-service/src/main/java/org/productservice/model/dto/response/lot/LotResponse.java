package org.productservice.model.dto.response.lot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotResponse {
    Long id;
    String name;
    Map<String, Object> attributes;
}
