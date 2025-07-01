package org.userservice.controller.user.favorite_offer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.dto.request.FavoriteOfferRequest;
import org.userservice.service.user.favorite.FavoriteOfferService;

@RestController
@RequestMapping("/api/favoriteOffers")
@RequiredArgsConstructor
public class FavoriteOfferController {

    private final FavoriteOfferService favoriteOfferService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody FavoriteOfferRequest request) {
        return favoriteOfferService.add(request);
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam Long userId,
                                 @RequestParam Long offerId) {
        return favoriteOfferService.get(FavoriteOfferRequest.builder().userId(userId).offerId(offerId).build());
    }

    @PostMapping("/getAllByUserId")
    public ResponseEntity<?> getAllByUserId(@RequestBody FavoriteOfferRequest request,
                                            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return favoriteOfferService.searchAllByUserId(request, pageable);
    }

    @GetMapping("/getCountByUserId")
    public ResponseEntity<Integer> getCountByUserId(@RequestParam Long userId) {
        return favoriteOfferService.getCountByUserId(FavoriteOfferRequest.builder().userId(userId).build());
    }


    @GetMapping("/getCountByOfferId")
    public ResponseEntity<Integer> getCountByOfferId(@RequestParam Long offerId) {
        return favoriteOfferService.getCountByOfferId(FavoriteOfferRequest.builder().offerId(offerId).build());
    }

    @GetMapping("/getTopLikedOfferIds")
    public ResponseEntity<?> getTopLikedOfferIds(@RequestParam(defaultValue = "10") Integer limit) {
        return favoriteOfferService.getTopLikedOfferIds(limit);
    }

    @GetMapping("/checkIfExist")
    public ResponseEntity<Boolean> checkIfExist(@RequestParam Long userId,
                                                @RequestParam Long offerId) {
        return favoriteOfferService.checkIfExist(FavoriteOfferRequest.builder().userId(userId).offerId(offerId).build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody FavoriteOfferRequest request) {
        return favoriteOfferService.delete(request);
    }



}
