package org.productservice.service.user;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Product;
import org.productservice.repository.LotRepository;
import org.productservice.repository.ProductRepository;
import org.productservice.service.utils.LotUtils;
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

    private final LotUtils lotUtils;

    @Transactional(readOnly = true)
    public ResponseEntity<?> get(LotRequest request) {
        Lot lot = lotUtils.findByRequest(request, lotRepository);
        LotResponse response = lotUtils.buildResponse(lot);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll(LotRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        if (request.getProductId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id is null");

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + request.getProductId()));

        List<LotResponse> lotResponses = product.getLots().stream().map(lotUtils::buildResponse).toList();
        if (lotResponses.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.status(HttpStatus.OK).body(lotResponses);
    }
}
