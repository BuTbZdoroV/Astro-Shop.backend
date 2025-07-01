package org.userservice.message.rest.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.userservice.message.config.FeignConfig;
import org.userservice.message.dto.response.OfferResponse;

import java.util.List;


@FeignClient(
        name = "product-service",
        url = "${product-service.url}",
        configuration = FeignConfig.class
)
public interface ProductServiceClient {
    @GetMapping("/api/offers/{offerId}/exists")
    ResponseEntity<Boolean> checkOfferExists(@PathVariable Long offerId);
}
