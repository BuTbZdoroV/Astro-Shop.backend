package org.productservice.service.admin;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.model.entity.Offer;
import org.productservice.repository.OfferRepository;
import org.productservice.service.utils.OfferUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"offers"})
public class OfferAdminService {

    private final OfferRepository offerRepository;
    private final OfferUtils offerUtils;

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'offer:' + #offerRequest.id"),
            @CacheEvict(key = "'offer:lot:' + #offer.lot.id"),
            @CacheEvict(key = "'offer:user:' + #offer.userId"),
            @CacheEvict(key = "'offer:product:' + #offer.lot.product.id")
    })
    public ResponseEntity<?> delete(OfferRequest offerRequest) {
        if (offerRequest == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request must not be null");
        if (offerRequest.getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offer id must not be null");

        Offer offer = offerUtils.findByRequest(offerRequest, offerRepository);
        offerRepository.delete(offer);

        return ResponseEntity.ok("Offer with id" + offerRequest.getId() + " deleted successfully");
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'offer:' + #offerRequest.id"),
            @CacheEvict(key = "'offer:attrs:' + #offerRequest.id")
    })
    public ResponseEntity<?> clearAttributes(OfferRequest offerRequest) {
        if (offerRequest == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request must not be null");

        Offer offer = offerUtils.findByRequest(offerRequest, offerRepository);
        offer.setAttributes(null);
        offerRepository.save(offer);

        return ResponseEntity.ok("Offer attributes with id" + offerRequest.getId() + " cleared successfully");
    }

}
