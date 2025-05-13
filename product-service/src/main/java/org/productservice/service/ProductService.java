package org.productservice.service;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    @Transactional
    public ResponseEntity<?> addProduct(ProductRequest request) {
        try {
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
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
