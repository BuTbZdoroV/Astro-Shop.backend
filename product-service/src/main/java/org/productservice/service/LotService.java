package org.productservice.service;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.response.lot.LotResponse;
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

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class LotService {
    private final Logger logger = LoggerFactory.getLogger(LotService.class);

    private final LotRepository lotRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ResponseEntity<?> createLot(LotRequest request) {
        if (request == null) {
            logger.warn("Request is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
        if (request.getProductId() == null) {
            logger.error("Product id is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id is null");
        }

        try {
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
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
