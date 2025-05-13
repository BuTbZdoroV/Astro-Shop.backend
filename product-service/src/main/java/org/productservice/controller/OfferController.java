package org.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.offer.OfferFilterRequest;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.service.OfferService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Tag(name = "Offer Management", description = "API для управления оферами")
@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor
public final class OfferController {
    private final OfferService offerService;

    @PostMapping("/add")
    public ResponseEntity<?> addOffer(@RequestBody @Valid OfferRequest offerRequest) {
        return offerService.addOffer(offerRequest);
    }

    @Operation(summary = "Обновление офера", description = "Обновление по заданным параметрам, значение равные null будут игнорироваться")
    @PatchMapping("/update")
    public ResponseEntity<?> updateOffer(@RequestBody @Valid OfferRequest offerRequest) {
        return offerService.updateOffer(offerRequest);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getOffer(@RequestBody @Valid OfferRequest offerRequest) {
        return offerService.getOffer(offerRequest);
    }

    @Operation(summary = "Поиск офера", description = "Фильтрация по заданным параметрам")
    @GetMapping("/search")
    public ResponseEntity<?> searchOffer(@ModelAttribute OfferRequest offerRequest,
                                         @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
        return offerService.search(offerRequest, pageable);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteOffer(@RequestBody OfferRequest offerRequest) {
        return offerService.deleteOffer(offerRequest);
    }

}
