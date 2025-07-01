package org.userservice.service.utils;

import org.springframework.stereotype.Component;
import org.userservice.model.dto.response.FavoriteOfferResponse;
import org.userservice.model.entity.FavoriteOffer;

@Component
public class FavoriteOfferUtils {

    public FavoriteOfferResponse buildResponse(FavoriteOffer favoriteOffer) {
        return FavoriteOfferResponse.builder()
                .id(favoriteOffer.getId())
                .userId(favoriteOffer.getUserId())
                .offerId(favoriteOffer.getOfferId())
                .createdAt(favoriteOffer.getCreatedAt())
                .build();
    }

}
