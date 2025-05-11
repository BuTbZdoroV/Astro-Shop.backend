package org.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.ProductFilterRequest;
import org.productservice.model.dto.request.ProductRequest;
import org.productservice.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Management", description = "API для управления продуктами")
@RequiredArgsConstructor
public final class ProductController {
    private final ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody @Valid ProductRequest productRequest) {
        return productService.addProduct(productRequest);
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @Operation(summary = "Поиск товаров", description = "Фильтрация по названию")
    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@ModelAttribute ProductFilterRequest productRequest,
                                           @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
        return productService.search(productRequest, pageable);
    }
}
