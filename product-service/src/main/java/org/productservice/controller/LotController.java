package org.productservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.service.LotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lot Management", description = "API для управления лотами")
@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
public final class LotController {
    private final LotService lotService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LotRequest request) {
        return lotService.create(request);
    }
}
