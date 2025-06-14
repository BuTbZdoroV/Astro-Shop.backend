package org.productservice.service.admin;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.productservice.service.user.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ProductAdminService {
    private final Logger logger = LoggerFactory.getLogger(ProductAdminService.class);

    private final ProductService productService;

    private final ProductRepository productRepository;

    @Transactional
    public ResponseEntity<?> create(ProductRequest request) {
        if (request == null) {
            logger.warn("Request is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            logger.warn("Request name is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request name cannot be empty");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Product savedProduct = productRepository.save(product);

        ProductResponse productResponse = ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .attributes(new HashMap<>())
                .build();

        logger.info("Product added successfully {}", productResponse.toString());
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }
}
