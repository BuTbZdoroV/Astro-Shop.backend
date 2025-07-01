package org.userservice.service.user.favorite;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.message.rest.feign.ProductServiceClient;
import org.userservice.model.dto.request.FavoriteOfferRequest;
import org.userservice.model.dto.response.FavoriteOfferResponse;
import org.userservice.model.entity.FavoriteOffer;
import org.userservice.repository.FavoriteOfferRepository;
import org.userservice.repository.UserRepository;
import org.userservice.service.utils.FavoriteOfferUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteOfferService {

    private final Logger logger = LoggerFactory.getLogger(FavoriteOfferService.class);
    private final FavoriteOfferRepository favoriteOfferRepository;
    private final ProductServiceClient productServiceClient;
    private final UserRepository userRepository;
    private final FavoriteOfferUtils favoriteOfferUtils;

    @Transactional
    public ResponseEntity<?> add(FavoriteOfferRequest request) {
        if (favoriteOfferRepository.existsByUserIdAndOfferId(request.getUserId(), request.getOfferId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This offer already exists");
        }

        Boolean offerExist = productServiceClient.checkOfferExists(request.getOfferId()).getBody();

        if (Boolean.FALSE.equals(offerExist)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This offer does not exist");
        }

        logger.info("Adding favorite offer to user {}", request.getUserId());

        FavoriteOffer favoriteOffer = FavoriteOffer.builder()
                .userId(request.getUserId())
                .offerId(request.getOfferId())
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteOffer savedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer);
        FavoriteOfferResponse response = favoriteOfferUtils.buildResponse(savedFavoriteOffer);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> get(FavoriteOfferRequest request) {
        if (request.getUserId() == null || request.getOfferId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters");
        }

        FavoriteOffer favoriteOffer = favoriteOfferRepository.findByUserIdAndOfferId(request.getUserId(), request.getOfferId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        FavoriteOfferResponse response = favoriteOfferUtils.buildResponse(favoriteOffer);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> searchAllByUserId(FavoriteOfferRequest request, Pageable pageable) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters");

        Page<FavoriteOffer> favoriteOffers = favoriteOfferRepository.findAllByUserId(request.getUserId(), pageable);
        Page<FavoriteOfferResponse> favoriteOfferResponses = favoriteOffers.map(favoriteOfferUtils::buildResponse);

        return new ResponseEntity<>(favoriteOfferResponses, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getCountByUserId(FavoriteOfferRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters");
        return new ResponseEntity<>(favoriteOfferRepository.countByUserId(request.getUserId()), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getCountByOfferId(FavoriteOfferRequest request) {
        if (request.getOfferId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters");
        return new ResponseEntity<>(favoriteOfferRepository.countByOfferId(request.getOfferId()), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getTopLikedOfferIds(int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = favoriteOfferRepository.findTopOffersByLikes(pageable);

        record TopLikedOfferDto(Long offerId, Long likeCount) {
        }

        List<TopLikedOfferDto> response = results.stream()
                .map(data -> new TopLikedOfferDto((Long) data[0], (Long) data[1]))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Boolean> checkIfExist(FavoriteOfferRequest request) {
        return new ResponseEntity<>(favoriteOfferRepository.existsByUserIdAndOfferId(request.getUserId(), request.getOfferId()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> delete(FavoriteOfferRequest request) {
        if (!favoriteOfferRepository.existsByUserIdAndOfferId(request.getUserId(), request.getOfferId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This offer does not exist");
        }

        favoriteOfferRepository.deleteByUserIdAndOfferId(request.getUserId(), request.getOfferId());
        return ResponseEntity.ok().build();
    }

    @KafkaListener(topics = "offer.delete", groupId = "product_service")
    public void handleOfferDelete(String message) {
        try {
            Long offerId = Long.parseLong(message);
            favoriteOfferRepository.deleteAllByOfferId(offerId);
            logger.info("Deleted offer with id {}", offerId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}
