package org.productservice.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.service.user.OfferService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Tag(name = "Offer Management", description = "API для управления оферами")
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public final class OfferController {
    private final OfferService offerService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid OfferRequest offerRequest, @RequestHeader("X-User-Id") Long userId) {
        if (userId == null || userId == -1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is null");
        return offerService.create(offerRequest, userId);
    }

    @Operation(summary = "Обновление офера", description = "Обновление по заданным параметрам, значение равные null будут игнорироваться")
    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody OfferRequest offerRequest, @RequestHeader("X-User-Id") Long userId) {
        if (userId == null || userId == -1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is null");
        return offerService.update(offerRequest);
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam Long offerId) {
        return offerService.get(OfferRequest.builder()
                .id(offerId)
                .build());
    }

    @Operation(description = "Получение всех активных оферов по лоту")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@RequestParam Long lotId) {
        return offerService.getAll(OfferRequest.builder()
                .lotId(lotId)
                .active(true)
                .build());
    }

    @PostMapping("/searchAllByUser")
    public ResponseEntity<?> searchAllByUser(@RequestBody @Valid OfferRequest offerRequest,
                                             @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable,
                                             @RequestHeader(value = "X-User-Id") Long userId) {
        if (userId == null || userId == -1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is null");

        return offerService.search(offerRequest, pageable);
    }

    @Operation(summary = "Поиск офера", description = "Фильтрация по заданным параметрам, offer Request - null допускается")
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody OfferRequest offerRequest,
                                    @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
        return offerService.search(offerRequest, pageable);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OfferRequest offerRequest,
                                    @RequestHeader(value = "X-User-Id") Long userId) {
        return offerService.delete(offerRequest, userId);
    }

}
