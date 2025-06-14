package org.productservice.service.admin;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Product;
import org.productservice.repository.LotRepository;
import org.productservice.repository.ProductRepository;
import org.productservice.service.utils.LotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class LotAdminService {
    private final Logger logger = LoggerFactory.getLogger(LotAdminService.class);

    private final ProductRepository productRepository;
    private final LotRepository lotRepository;
    private final LotUtils lotUtils;


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
        LotResponse lotResponse = lotUtils.buildResponse(savedLot);

        logger.info("Created lot with id: {}", savedLot.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(lotResponse);
    }

}
