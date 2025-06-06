package org.productservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product Management", description = "API для управления товарами")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public final class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().build();
    }
}
