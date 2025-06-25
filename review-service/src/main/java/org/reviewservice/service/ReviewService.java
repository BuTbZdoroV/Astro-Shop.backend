package org.reviewservice.service;

import lombok.RequiredArgsConstructor;
import org.reviewservice.model.dto.other.ReviewStats;
import org.reviewservice.model.dto.other.SellerStats;
import org.reviewservice.model.dto.request.ReviewRequest;
import org.reviewservice.model.dto.response.ReviewResponse;
import org.reviewservice.model.entity.Review;
import org.reviewservice.repository.ReviewRepository;
import org.reviewservice.service.utils.ReviewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    private final ReviewUtils reviewUtils;

    @Transactional
    @CacheEvict(value = {"sellerStats", "allBySellerId"}, key = "#request.sellerId")
    public ResponseEntity<?> create(ReviewRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (request.getOfferId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offerId is required");
        if (request.getBuyerId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "buyerId is required");
        if (request.getSellerId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sellerId is required");

        if (request.getRating() > 10) request.setRating(10);

        Review review = Review.builder()
                .buyerId(request.getBuyerId())
                .sellerId(request.getSellerId())
                .offerId(request.getOfferId())
                .comment(request.getComment())
                .rating(request.getRating())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);
        ReviewResponse response = reviewUtils.buildResponse(savedReview);

        logger.info("Review created: {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "sellerStats", key = "#request.sellerId")
    public ResponseEntity<?> getSellerStats(ReviewRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (request.getSellerId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sellerId is required");

        logger.info("getSellerStats request: {}", request);

        ReviewStats stats = reviewRepository.getSellerRatingStats(request.getSellerId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        SellerStats sellerStats = SellerStats.builder()
                .avgRating(stats.getAvgRating())
                .reviewsCount(stats.getReviewsCount())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(sellerStats);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allBySellerId", key = "#request.sellerId")
    public ResponseEntity<?> getAllBySellerId(ReviewRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (request.getSellerId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sellerId is required");

        List<Review> reviews = reviewRepository.findAllBySellerId(request.getSellerId());

        if (reviews.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<ReviewResponse> response = reviews.stream().map(reviewUtils::buildResponse).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
