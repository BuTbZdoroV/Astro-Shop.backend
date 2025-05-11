package org.productservice.model.dto.response.productresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.productservice.model.dto.response.ProductResponse;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse extends ProductResponse {
    private String genre;
    private String platform;
    private Integer minAge;
}
