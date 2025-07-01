package org.productservice.controller.admin;


import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.service.admin.OfferAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/offers/admin")
public class OfferAdminController {
    private final OfferAdminService offerAdminService;

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OfferRequest offerRequest) {
        return offerAdminService.delete(offerRequest);
    }

    @PatchMapping("/clearAttributes")
    public ResponseEntity<?> clearAttributes(@RequestBody OfferRequest offerRequest) {
        return offerAdminService.clearAttributes(offerRequest);
    }

}
