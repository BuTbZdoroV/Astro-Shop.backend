package org.productservice.service.admin;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.productservice.service.user.ProductService;
import org.productservice.service.utils.ProductUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductAdminService {
    private final Logger logger = LoggerFactory.getLogger(ProductAdminService.class);

    private final ProductRepository productRepository;
    private final ProductUtils productUtils;


    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'product:' + #result.body.id", value = "products"),
                    @CachePut(key = "'product:name:' + #result.body.name", value = "products")
            },
            evict = {
                    @CacheEvict(key = "'product:all'", value = "products")
            }
    )
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
                .attributes(request.getAttributes())
                .build();

        Product savedProduct = productRepository.save(product);

        ProductResponse productResponse = ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .attributes(savedProduct.getAttributes())
                .build();

        logger.info("Product added successfully {}", productResponse.toString());
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }


    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'product:' + #result.body.id", value = "products", condition = "#result != null"),
                    @CachePut(key = "'product:name:' + #result.body.name", value = "products", condition = "#result != null")
            },
            evict = {
                    @CacheEvict(key = "'product:all'", value = "products")
            }
    )
    public ResponseEntity<?> update(ProductRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");

        if (request.getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request id cannot be null");

        Product product = productRepository.findById(request.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Map<String, Object> changedData = new HashMap<>();

        if (request.getName() != null && !request.getName().isEmpty()) {
            product.setName(request.getName());
            changedData.put("name", product.getName());
        }

        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            product.setDescription(request.getDescription());
            changedData.put("description", product.getDescription());
        }

        if (request.getAttributes() != null) {
            product.setAttributes(request.getAttributes());
            changedData.put("attributes", product.getAttributes());
        }

        if (changedData.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        Product savedProduct = productRepository.save(product);

        ProductResponse response = ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .attributes(savedProduct.getAttributes())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(key = "'product:' + #request.id", value = "products"),
                    @CacheEvict(key = "'product:name:' + #product.name", value = "products"),
                    @CacheEvict(key = "'product:all'", value = "products")
            }
    )
    public ResponseEntity<?> delete(ProductRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");

        Product product = productUtils.findByRequest(request, productRepository);
        productRepository.delete(product);

        return ResponseEntity.ok().build();
    }

}
