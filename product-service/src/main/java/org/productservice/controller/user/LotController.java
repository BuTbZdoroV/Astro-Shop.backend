package org.productservice.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.service.user.LotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lot Management", description = "API для управления лотами")
@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public final class LotController {
    private final LotService lotService;

    @GetMapping("/get")
    public ResponseEntity<?> get(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name) {
        return lotService.get(new LotRequest(id, name, null));
    }

    @Operation(description = "Поиск всех лотов по productID")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@RequestParam Long productId) {
        return lotService.getAll(new LotRequest(null, null, productId));
    }


}
