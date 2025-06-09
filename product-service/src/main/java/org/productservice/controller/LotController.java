package org.productservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.service.LotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lot Management", description = "API для управления лотами")
@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public final class LotController {
    private final LotService lotService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LotRequest request) {
        return lotService.create(request);
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name) {
        return lotService.get(new LotRequest(id, name, null));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@RequestParam Long productId) {
        return lotService.getAll(new LotRequest(null, null, productId));
    }


}
