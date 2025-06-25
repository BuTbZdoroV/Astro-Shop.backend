package org.productservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.service.admin.ProductAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/admin")
public class ProductAdminController {
    private final ProductAdminService productAdminService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
        return productAdminService.create(request);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody ProductRequest request) {
        return productAdminService.update(request);
    }
}
