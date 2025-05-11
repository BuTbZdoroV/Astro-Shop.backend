package org.productservice.model.dto.request;

import lombok.Data;
import org.productservice.model.entity.Product;

@Data
public class ProductFilterRequest {
    String name;
    String description;
    Product.Category category;
}
