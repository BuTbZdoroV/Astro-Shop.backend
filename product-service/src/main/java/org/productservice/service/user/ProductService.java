package org.productservice.service.user;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.productservice.service.utils.ProductUtils;
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

@Service
@RequiredArgsConstructor
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ProductUtils productUtils;

    @Transactional(readOnly = true)
    public ResponseEntity<?> get(ProductRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");

        Optional<Product> productOptional = Optional.empty();

        if (request.getId() != null) {
            productOptional = productRepository.findByIdWithLots(request.getId());
        }
        if (request.getName() != null && productOptional.isEmpty()) {
            productOptional = productRepository.findByNameWithLots(request.getName());
        }

        Product product = productOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with name: " + request.getName()));

        ProductResponse response = productUtils.buildResponse(product);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll() {
        List<Product> products = productRepository.findAllWithLots();
        if (products.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<ProductResponse> productResponses = products.stream().map(productUtils::buildResponse).toList();

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }
}
