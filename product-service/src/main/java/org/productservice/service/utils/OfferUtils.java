package org.productservice.service.utils;

import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.model.dto.response.offer.OfferResponse;
import org.productservice.model.entity.Offer;
import org.productservice.repository.OfferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@Component
public class OfferUtils {

    public Offer findByRequest(OfferRequest offerRequest, OfferRepository offerRepository) {
        if (offerRequest == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request cannot be null");

        Optional<Offer> offerOptional = Optional.empty();

        if (offerRequest.getId() != null) {
            offerOptional = offerRepository.findById(offerRequest.getId());
        }

        return offerOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found with id: " + offerRequest.getId()));
    }

    public OfferResponse buildResponse(Offer offer) {
        return OfferResponse.builder()
                .id(offer.getId())
                .name(offer.getName())
                .createdAt(offer.getCreatedAt())
                .price(offer.getPrice())
                .shortDescription(offer.getShortDescription())
                .longDescription(offer.getLongDescription())
                .availability(offer.getAvailability())
                .attributes(offer.getAttributes() != null ?
                        offer.getAttributes() :
                        new HashMap<>())
                .active(offer.getActive())
                .userId(offer.getUserId())
                .build();
    }

}
