package org.reviewservice.service;

import lombok.RequiredArgsConstructor;
import org.reviewservice.model.dto.other.ReviewStats;
import org.reviewservice.model.dto.other.SellerStats;
import org.reviewservice.model.dto.request.ReviewRequest;
import org.reviewservice.model.dto.response.ReviewResponse;
import org.reviewservice.model.entity.Review;
import org.reviewservice.repository.ReviewRepository;
import org.reviewservice.service.utils.ReviewUtils;
import org.reviewservice.service.utils.specification.ReviewSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"sellerStats", "reviewsBySeller", "reviewsByOffer"})
public class ReviewService {
    private final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    private final ReviewUtils reviewUtils;
    private final ReviewSpecifications reviewSpecifications;

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'stats:' + #request.sellerId", value = "sellerStats"),
            @CacheEvict(key = "'seller:' + #request.sellerId", value = "reviewsBySeller"),
            @CacheEvict(key = "'offer:' + #request.offerId", value = "reviewsByOffer"),
            @CacheEvict(key = "#request.sellerId", value = "sellerReviewCount")
    })
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
    @Cacheable(key = "'stats:' + #request.sellerId", unless = "#result.body.avgRating == null")
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
    @Cacheable(key = "'seller:' + #request.sellerId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public ResponseEntity<?> searchAllBySellerId(ReviewRequest request, Pageable pageable) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (request.getSellerId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sellerId is required");

        Specification<Review> specification = reviewSpecifications.hasSellerId(request.getSellerId());
        Page<Review> reviews = reviewRepository.findAll(specification, pageable);

        if (reviews.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Page<ReviewResponse> response = reviews.map(reviewUtils::buildResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'offer:' + #request.offerId",
            unless = "#result == null || (#result.getBody() != null && #result.getBody().isEmpty())")
    public ResponseEntity<?> getAllByOfferId(ReviewRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request cannot be null");
        }
        if (request.getOfferId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offerId is required");
        }

        List<Review> reviews = reviewRepository.findAllByOfferId(request.getOfferId());

        if (reviews.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Возвращаем пустой список с 200 OK
        }

        List<ReviewResponse> response = reviews.stream()
                .map(reviewUtils::buildResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#sellerId", value = "sellerReviewCount")
    public ResponseEntity<?> getCountBySellerId(Long sellerId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewRepository.getCountBySellerId(sellerId));
    }

}
