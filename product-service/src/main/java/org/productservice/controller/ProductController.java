package org.productservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product Management", description = "API для управления товарами")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public final class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String name) {
        return productService.get(new ProductRequest(id, name, null));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return productService.getAll();
    }

    @GetMapping("/test")
    public ResponseEntity<String> getUserInfo(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Roles") String roles
    ) {
        return ResponseEntity.ok(
                "User ID: " + userId + "\n" +
                        "Email: " + email + "\n" +
                        "Roles: " + roles
        );
    }
}
