package org.productservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.service.LotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lot Management", description = "API для управления лотами")
@RestController
@RequestMapping("/let")
@RequiredArgsConstructor
public final class LotController {
    private final LotService lotService;

    @PostMapping("/createLot")
    public ResponseEntity<?> createLot(@RequestBody @Valid LotRequest request) {
        return lotService.createLot(request);
    }
}
