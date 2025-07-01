package org.productservice.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.service.user.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product Management", description = "API для управления товарами")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public final class ProductController {
    private final ProductService productService;

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String name) {
        return productService.get(ProductRequest.builder()
                .id(id)
                .name(name)
                .build());
    }

    @GetMapping("/getByOfferId")
    public ResponseEntity<?> getByOfferId(@RequestParam Long offerId) {
        return productService.getByOfferId(offerId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return productService.getAll();
    }

}
