package org.productservice.service.utils;

import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@Component
public class ProductUtils {

    public Product findByRequest(ProductRequest request, ProductRepository productRepository) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");

        Optional<Product> productOptional = Optional.empty();

        if (request.getId() != null) {
            productOptional = productRepository.findById(request.getId());
        }
        if (request.getName() != null && productOptional.isEmpty()) {
            productOptional = productRepository.findByName(request.getName());
        }

        return productOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

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
                .attributes(product.getAttributes())
                .build();
    }

}
