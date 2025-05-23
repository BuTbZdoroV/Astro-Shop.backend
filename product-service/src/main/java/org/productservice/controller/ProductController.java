package org.productservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product Management", description = "API для управления товарами")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public final class ProductController {
    private final ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody @Valid ProductRequest request) {
        return productService.addProduct(request);
    }
}
