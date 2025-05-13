package org.productservice.service;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.offer.OfferFilterRequest;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.model.dto.response.offer.OfferResponse;
import org.productservice.model.dto.specification.offer.OfferSpecifications;
import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Offer;
import org.productservice.repository.LotRepository;
import org.productservice.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferService {
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;
    private final LotRepository lotRepository;

    @Transactional
    public ResponseEntity<?> addOffer(OfferRequest offerRequest) {
        try {
            Lot lot = lotRepository.findById(offerRequest.getLotId()).orElseThrow(() -> {
                logger.error("Lot with id {} not found", offerRequest.getLotId());
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot not found");
            });

            Offer offer = Offer.builder()
                    .name(offerRequest.getName())
                    .price(offerRequest.getPrice())
                    .shortDescription(offerRequest.getShortDescription())
                    .longDescription(offerRequest.getLongDescription())
                    .createdAt(new Date())
                    .lot(lot)
                    .attributes(offerRequest.getAttributes())
                    .build();

            lot.getOffers().add(offer);
            lotRepository.save(lot);

            Offer savedOffer = offerRepository.save(offer);

            OfferResponse offerResponse = OfferResponse.builder()
                    .id(savedOffer.getId())
                    .name(savedOffer.getName())
                    .createdAt(offer.getCreatedAt())
                    .price(offer.getPrice())
                    .shortDescription(offer.getShortDescription())
                    .longDescription(offer.getLongDescription())
                    .attributes(offer.getAttributes())
                    .active(offer.getActive())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(offerResponse);

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Обновление текущего офера по заданным параметрам.
     *
     * @param offerRequest параметры, все кроме id могут быть null
     * @return Возвращает HTTP 200 с dto данными или 304 если изменений не было.
     * @throws ResponseStatusException HTTP 204 Если офер не найден
     */
    @Transactional
    public ResponseEntity<?> updateOffer(OfferRequest offerRequest) {
        if (offerRequest == null) return new ResponseEntity<>("offerRequest is null", HttpStatus.BAD_REQUEST);
        if (offerRequest.getId() == null) return new ResponseEntity<>("id is null", HttpStatus.BAD_REQUEST);

        Offer offer = offerRepository.findById(offerRequest.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found for ID: " + offerRequest.getId()));

        Map<String, Object> changedData = new HashMap<>();

        if (offerRequest.getName() != null && !offerRequest.getName().isEmpty()) {
            offer.setName(offerRequest.getName());
            changedData.put("name", offerRequest.getName());
        }
        if (offerRequest.getShortDescription() != null) {
            offer.setShortDescription(offerRequest.getShortDescription());
            changedData.put("shortDescription", offerRequest.getShortDescription());
        }
        if (offerRequest.getLongDescription() != null) {
            offer.setLongDescription(offerRequest.getLongDescription());
            changedData.put("longDescription", offerRequest.getLongDescription());
        }
        if (offerRequest.getAttributes() != null) {
            offer.setAttributes(offerRequest.getAttributes());
            changedData.put("attributes", offerRequest.getAttributes());
        }

        if (changedData.isEmpty()) {
            logger.warn("Offer with id {} not changed", offerRequest.getId());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        Offer savedOffer = offerRepository.save(offer);
        logger.info("Updated offer with ID: {}", savedOffer.getId());
        logger.info("Updated data: {}", changedData);

        OfferResponse offerResponse = OfferResponse.builder()
                .id(savedOffer.getId())
                .name(savedOffer.getName())
                .createdAt(offer.getCreatedAt())
                .price(offer.getPrice())
                .shortDescription(offer.getShortDescription())
                .longDescription(offer.getLongDescription())
                .attributes(offer.getAttributes())
                .active(offer.getActive())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(offerResponse);
    }

    @Transactional
    public ResponseEntity<?> deleteOffer(OfferRequest offerRequest) {
        if (offerRequest == null) return new ResponseEntity<>("offerRequest is null", HttpStatus.BAD_REQUEST);
        if (offerRequest.getId() == null) return new ResponseEntity<>("id is null", HttpStatus.BAD_REQUEST);
        Offer offer = offerRepository.findById(offerRequest.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        offerRepository.delete(offer);
        return new ResponseEntity<>("Offer delete by ID: " + offerRequest.getId(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getOffer(OfferRequest offerRequest) {
        if (offerRequest == null) return new ResponseEntity<>("offerRequest is null", HttpStatus.BAD_REQUEST);
        if (offerRequest.getId() == null) return new ResponseEntity<>("id is null", HttpStatus.BAD_REQUEST);

        Offer offer = offerRepository.findById(offerRequest.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        OfferResponse offerResponse = OfferResponse.builder()
                .id(offer.getId())
                .name(offer.getName())
                .createdAt(offer.getCreatedAt())
                .price(offer.getPrice())
                .shortDescription(offer.getShortDescription())
                .longDescription(offer.getLongDescription())
                .attributes(offer.getAttributes())
                .active(offer.getActive())
                .build();

        return ResponseEntity.ok(offerResponse);
    }


    /**
     * Поиск продуктов с динамической фильтрацией и пагинацией.
     *
     * @param request  - DTO с параметрами фильтрации, Если поле null, оно не учитывается в фильтрации.
     * @param pageable Параметры пагинации и сортировки (номер страницы, размер, сортировка).
     * @return Страница (Page) с отфильтрованными продуктами в формате {@link OfferResponse}.
     * Возвращает HTTP 200 с данными.
     * @throws ResponseStatusException HTTP 204 (No Content), если продукты не найдены.
     * @example GET /api/offers?name=Phone&page=0&size=10
     * @see OfferSpecifications
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> search(OfferRequest request, Pageable pageable) {
        try {
            Specification<Offer> specification = Specification.where(null);

            if (request.getId() != null) {
                specification = specification.and(OfferSpecifications.hasId(request.getId()));
            }

            if (request.getName() != null) {
                specification = specification.and(OfferSpecifications.nameLike(request.getName()));
            }

            if (request.getLotId() != null) {
                specification = specification.and(OfferSpecifications.hasLotId(request.getLotId()));
            }


            Page<Offer> offersPage = offerRepository.findAll(specification, pageable);
            Page<OfferResponse> responsePage = offersPage.map(offer -> OfferResponse.builder()
                    .id(offer.getId())
                    .name(offer.getName())
                    .createdAt(offer.getCreatedAt())
                    .price(offer.getPrice())
                    .shortDescription(offer.getShortDescription())
                    .longDescription(offer.getLongDescription())
                    .attributes(offer.getAttributes())
                    .active(offer.getActive())
                    .build());

            if (responsePage.isEmpty()) {
                logger.warn("No offers found with filter: {}", request);
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(responsePage);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
