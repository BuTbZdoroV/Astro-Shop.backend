package org.productservice.service;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.dto.response.offer.OfferResponse;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Product;
import org.productservice.repository.LotRepository;
import org.productservice.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LotService {
    private final Logger logger = LoggerFactory.getLogger(LotService.class);

    private final LotRepository lotRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ResponseEntity<?> create(LotRequest request) {
        if (request == null) {
            logger.warn("Request is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
        if (request.getProductId() == null) {
            logger.warn("Product id is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id is null");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            logger.warn("Lot name is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lot name is null");
        }

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + request.getProductId()));

        Lot lot = Lot.builder()
                .name(request.getName())
                .product(product)
                .build();

        product.getLots().add(lot);
        lotRepository.save(lot);

        Lot savedLot = lotRepository.save(lot);

        LotResponse lotResponse = LotResponse.builder()
                .id(savedLot.getId())
                .name(savedLot.getName())
                .attributes(new HashMap<>())
                .build();


        logger.info("Created lot with id: {}", savedLot.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(lotResponse);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> get(LotRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");

        Optional<Lot> lotOptional = Optional.empty();
        if (request.getId() != null) {
            lotOptional = lotRepository.findById(request.getId());
        }
        if (request.getName() != null && lotOptional.isEmpty()) {
            lotOptional = lotRepository.findByName(request.getName());
        }
        Lot lot = lotOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot not found with name: " + request.getName()));

        LotResponse response = LotResponse.builder()
                .id(lot.getId())
                .name(lot.getName())
                .product(ProductResponse.builder()
                        .id(lot.getProduct().getId())
                        .name(lot.getProduct().getName())
                        .build())
                .offers(lot.getOffers().stream().map(offer -> OfferResponse.builder()
                        .id(offer.getId())
                        .name(offer.getName())
                        .price(offer.getPrice())
                        .createdAt(offer.getCreatedAt())
                        .availability(offer.getAvailability())
                        .attributes(offer.getAttributes())
                        .build()).toList())
                .attributes(new HashMap<>())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll(LotRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        if (request.getProductId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id is null");

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + request.getProductId()));

        List<LotResponse> lotResponses = product.getLots().stream().map(lot -> LotResponse.builder()
                .id(lot.getId())
                .name(lot.getName())
                .attributes(new HashMap<>())
                .build()).toList();

        return ResponseEntity.status(HttpStatus.OK).body(lotResponses);
    }
}
