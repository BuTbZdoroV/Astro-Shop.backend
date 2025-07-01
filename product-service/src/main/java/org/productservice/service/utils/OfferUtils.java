package org.productservice.service.utils;

import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.model.dto.response.offer.OfferResponse;
import org.productservice.model.entity.Offer;
import org.productservice.model.entity.Product;
import org.productservice.repository.OfferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .pictureUrl(offer.getPictureUrl())
                .build();
    }

    public Map<String, Object> validateAttributes(Map<String, Object> inputAttributes, Product product) {
        Map<String, Object> validAttributes = new HashMap<>();
        Map<String, Object> productAttributes = product.getAttributes();

        if (productAttributes == null || productAttributes.isEmpty()) {
            return inputAttributes;
        }

        for (Map.Entry<String, Object> entry : inputAttributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Проверяем, существует ли атрибут в продукте
            if (productAttributes.containsKey(key)) {
                Object expectedType = productAttributes.get(key);

                // Если в продукте определен список значений
                if (expectedType instanceof List<?> allowedValues) {
                    if (allowedValues.contains(value)) {
                        validAttributes.put(key, value);
                    }
                }
                // Если определен тип (например, "number")
                else if (expectedType instanceof String type) {
                    switch (type) {
                        case "number":
                            if (value instanceof Number) {
                                validAttributes.put(key, value);
                            }
                            break;
                        case "string":
                            if (value instanceof String) {
                                validAttributes.put(key, value);
                            }
                            break;
                        default:
                            validAttributes.put(key, value);
                    }
                }
                // Простая проверка типа
                else {
                    if (value.getClass().equals(expectedType.getClass())) {
                        validAttributes.put(key, value);
                    }
                }
            }
        }

        return validAttributes;
    }

}
