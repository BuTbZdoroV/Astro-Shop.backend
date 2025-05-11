package org.productservice.model.dto.request.productrequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.productservice.model.dto.request.ProductRequest;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest extends ProductRequest {
    private String genre;
    private String platform;
    private Integer minAge;
}
