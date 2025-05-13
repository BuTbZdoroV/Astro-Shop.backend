package org.productservice.model.dto.request.lot;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotRequest {
    @NotBlank
    String name;

    Long productId;
}
