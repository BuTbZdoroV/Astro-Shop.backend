package org.reviewservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.reviewservice.model.dto.request.ReviewRequest;
import org.reviewservice.service.ReviewService;
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

    @GetMapping("/getAllBySellerId")
    public ResponseEntity<?> getAllBySellerId(@RequestParam Long sellerId) {
        return reviewService.getAllBySellerId(ReviewRequest.builder()
                .sellerId(sellerId)
                .build());
    }


}
