package org.reviewservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.reviewservice.model.dto.request.ReviewRequest;
import org.reviewservice.service.ReviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ReviewRequest request) {
        return reviewService.create(request);
    }

    @Operation(description = "Получение AVG Rating (0 - 10) и Reviews Count")
    @GetMapping("/getSellerStats")
    public ResponseEntity<?> getSellerStats(@RequestParam Long sellerId) {
        return reviewService.getSellerStats(ReviewRequest.builder()
                .sellerId(sellerId)
                .build());
    }

    @PostMapping("/searchAllBySellerId")
    public ResponseEntity<?> searchAllBySellerId(@RequestBody ReviewRequest request,
                                                 @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.searchAllBySellerId(ReviewRequest.builder()
                .sellerId(request.getSellerId())
                .build(), pageable);
    }

    @GetMapping("/getAllByOfferId")
    public ResponseEntity<?> getAllByOfferId(@RequestParam Long offerId) {
        return reviewService.getAllByOfferId(ReviewRequest.builder().offerId(offerId).build());
    }



}
