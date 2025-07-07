package org.productservice.service.user;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Offer;
import org.productservice.model.entity.Product;
import org.productservice.repository.OfferRepository;
import org.productservice.repository.ProductRepository;
import org.productservice.service.utils.ProductUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final OfferRepository offerRepository;
    private final ProductUtils productUtils;

    @Transactional(readOnly = true)
    @Cacheable(key = "{#request.id != null ? 'product:' + #request.id : 'product:name:' + #request.name}",
            unless = "#result.body == null")
    public ResponseEntity<?> get(ProductRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product request cannot be null");

        Optional<Product> productOptional = Optional.empty();

        if (request.getId() != null) {
            productOptional = productRepository.findByIdWithLots(request.getId());
        }
        if (request.getName() != null && productOptional.isEmpty()) {
            productOptional = productRepository.findByNameWithLots(request.getName());
        }

        Product product = productOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with name: " + request.getName()));

        ProductResponse response = productUtils.buildResponse(product);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'product:offer:' + #offerId", unless = "#result.body == null")
    public ResponseEntity<?> getByOfferId(Long offerId) {
        if (offerId == null || offerId < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offer id cannot be null");

        Offer offer = offerRepository.findById(offerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found with id: " + offerId));

        Product product = offer.getLot().getProduct();
        ProductResponse response = productUtils.buildResponse(product);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'product:all'", unless = "#result.body.isEmpty()")
    public ResponseEntity<?> getAll() {
        List<Product> products = productRepository.findAllWithLots();
        if (products.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<ProductResponse> productResponses = products.stream().map(productUtils::buildResponse).toList();

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'product:all'", unless = "#result.body.isEmpty()")
    public ResponseEntity<?> getAllNameWithLots() {
        List<Product> products = productRepository.findAllWithLots();

        if (products.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> productResponses = products.stream().map(product -> ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .lots(product.getLots().stream().map(lot -> LotResponse.builder()
                        .id(lot.getId())
                        .name(lot.getName())
                        .build()).collect(Collectors.toList()))
                .build()).toList();

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }
}
