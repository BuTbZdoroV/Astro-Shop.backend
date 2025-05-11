package org.productservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.productservice.model.entity.Product;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    Long id;
    String name;
    String description;

    public static ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription());
    }
}
