package org.productservice.service.utils;

import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ProductUtils {

    public ProductResponse buildResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .lots(product.getLots().stream().map(lot -> LotResponse.builder()
                        .id(lot.getId())
                        .name(lot.getName())
                        .attributes(new HashMap<>())
                        .build()).toList())
                .attributes(new HashMap<>())
                .build();
    }

}
