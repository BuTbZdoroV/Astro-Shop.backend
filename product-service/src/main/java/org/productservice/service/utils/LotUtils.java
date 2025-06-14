package org.productservice.service.utils;

import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.entity.Lot;
import org.productservice.repository.LotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@Component
public class LotUtils {

    public Lot findByRequest(LotRequest request, LotRepository lotRepository) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        if (lotRepository == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lot repository cannot be null");
        Optional<Lot> lotOptional = Optional.empty();
        if (request.getId() != null) {
            lotOptional = lotRepository.findById(request.getId());
        }
        if (request.getName() != null && lotOptional.isEmpty()) {
            lotOptional = lotRepository.findByName(request.getName());
        }
        return lotOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot not found with name: " + request.getName()));
    }

    public LotResponse buildResponse(Lot lot) {
        return LotResponse.builder()
                .id(lot.getId())
                .name(lot.getName())
                .attributes(new HashMap<>())
                .build();
    }

}
