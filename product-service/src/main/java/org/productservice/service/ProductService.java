package org.productservice.service;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
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

    @Transactional(readOnly = true)
    public ResponseEntity<?> get(ProductRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");

        Optional<Product> productOptional = Optional.empty();

        if (request.getId() != null) {
            productOptional = productRepository.findByIdWithLots(request.getId());
        }
        if (request.getName() != null && productOptional.isEmpty()) {
            productOptional = productRepository.findByNameWithLots(request.getName());
        }

        Product product = productOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with name: " + request.getName()));

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .lots(product.getLots().stream().map(lot -> LotResponse.builder()
                        .id(lot.getId())
                        .name(lot.getName())
                        .build()).toList())
                .attributes(new HashMap<>())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll() {
        System.out.println("getAll - called");
        List<Product> products = productRepository.findAllWithLots();
        List<ProductResponse> productResponses = products.stream().map(product -> ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .lots(product.getLots().stream().map(lot -> LotResponse.builder()
                        .id(lot.getId())
                        .name(lot.getName())
                        .build()).toList())
                .attributes(new HashMap<>())
                .build()).toList();

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }
}
