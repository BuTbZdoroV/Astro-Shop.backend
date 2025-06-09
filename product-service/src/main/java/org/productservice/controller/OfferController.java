package org.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.service.OfferService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Tag(name = "Offer Management", description = "API для управления оферами")
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public final class OfferController {
    private final OfferService offerService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid OfferRequest offerRequest) {
        return offerService.add(offerRequest);
    }

    @Operation(summary = "Обновление офера", description = "Обновление по заданным параметрам, значение равные null будут игнорироваться")
    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid OfferRequest offerRequest) {
        return offerService.update(offerRequest);
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam Long id) {
        return offerService.getAll(new OfferRequest(id, null, null, null, null, null, null, null));
    }

    @Operation(summary = "Поиск офера", description = "Фильтрация по заданным параметрам, offer Request - null допускается")
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody @Valid OfferRequest offerRequest,
                                    @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
        return offerService.search(offerRequest, pageable);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OfferRequest offerRequest) {
        return offerService.delete(offerRequest);
    }

}
