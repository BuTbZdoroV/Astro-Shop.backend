package org.productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.productservice.model.dto.request.offer.OfferRequest;
import org.productservice.model.dto.response.offer.OfferResponse;
import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Offer;
import org.productservice.model.entity.Product;
import org.productservice.repository.LotRepository;
import org.productservice.repository.OfferRepository;
import org.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class OfferServiceTest {

    private static final String ON_EXISTS_LOT_NAME = "ON_EXISTS_LOT";
    private static final String ON_EXISTS_PRODUCT_NAME = "ON_EXISTS_PRODUCT";
    private static final String ON_EXISTS_OFFER_NAME = "ON_EXISTS_OFFER";
    private static final String NON_EXISTS_OFFER_NAME = "NON_EXISTS_OFFER_NAME";
    private static final String NEW_NAME_FOR_OFFER = "NEW_NAME_FOR_OFFER";
    public static final double NEW_PRICE_FOR_OFFER = 100D;
    private static Long ON_EXISTS_LOT_ID;
    private static Long ON_EXISTS_OFFER_ID;
    private static final Long NON_EXISTS_OFFER_ID = 999L;
    private static final Long NON_EXISTS_LOT_ID = 999L;

    @Autowired
    OfferService offerService;

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    LotRepository lotRepository;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    @Transactional
    void setUp() {

        Product product = Product.builder()
                .name(ON_EXISTS_PRODUCT_NAME)
                .description("Product description")
                .build();

        Product savedProduct = productRepository.save(product);

        Lot lot = Lot.builder()
                .name(ON_EXISTS_LOT_NAME)
                .product(savedProduct)
                .build();

        Lot savedLot = lotRepository.save(lot);
        ON_EXISTS_LOT_ID = savedLot.getId();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("Fraction", "Horde");
        attributes.put("Condition", false);
        attributes.put("AdditionalFee", 10000D);

        Offer offer = Offer.builder()
                .name(ON_EXISTS_OFFER_NAME)
                .availability(1)
                .price(NEW_PRICE_FOR_OFFER)
                .createdAt(new Date())
                .lot(savedLot)
                .longDescription("Product long description")
                .shortDescription("Product short description")
                .attributes(attributes)
                .build();

        lotRepository.save(savedLot);
        Offer savedOffer = offerRepository.save(offer);
        ON_EXISTS_OFFER_ID = savedOffer.getId();


    }

    @Test
    @Transactional
    void testAdd_OfferRequestIsValid() {
        //Проверка 1
        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setLotId(ON_EXISTS_LOT_ID);
        offerRequest.setName(ON_EXISTS_OFFER_NAME);
        offerRequest.setPrice(NEW_PRICE_FOR_OFFER);

        ResponseEntity<OfferResponse> response = (ResponseEntity<OfferResponse>) offerService.add(offerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(ON_EXISTS_OFFER_NAME);
        assertThat(response.getBody().getLot().getId()).isEqualTo(ON_EXISTS_LOT_ID);
        assertThat(response.getBody().getPrice()).isEqualTo(NEW_PRICE_FOR_OFFER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(lotRepository.findById(response.getBody().getLot().getId())).isNotNull();

        //Проверка 2
        offerRequest = new OfferRequest();
        offerRequest.setLotId(ON_EXISTS_LOT_ID);
        offerRequest.setName(NON_EXISTS_OFFER_NAME);
        offerRequest.setPrice(NEW_PRICE_FOR_OFFER);

        response = (ResponseEntity<OfferResponse>) offerService.add(offerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(NON_EXISTS_OFFER_NAME);
        assertThat(response.getBody().getLot().getId()).isEqualTo(ON_EXISTS_LOT_ID);
        assertThat(response.getBody().getPrice()).isEqualTo(NEW_PRICE_FOR_OFFER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(lotRepository.findById(response.getBody().getLot().getId())).isNotNull();
    }

    @Test
    @Transactional
    void testAdd_OfferRequestIsNotValid() {
        assertThatThrownBy(() -> offerService.add(null)).isInstanceOf(ResponseStatusException.class);

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setLotId(NON_EXISTS_LOT_ID);
        offerRequest.setName(NON_EXISTS_OFFER_NAME);
        offerRequest.setPrice(NEW_PRICE_FOR_OFFER);

        OfferRequest finalOfferRequest3 = offerRequest;
        assertThatThrownBy(() -> offerService.add(finalOfferRequest3))
                .isInstanceOf(ResponseStatusException.class);

        offerRequest = new OfferRequest();
        offerRequest.setLotId(ON_EXISTS_LOT_ID);
        offerRequest.setName("");
        offerRequest.setPrice(NEW_PRICE_FOR_OFFER);
        OfferRequest finalOfferRequest2 = offerRequest;
        assertThatThrownBy(() -> offerService.add(finalOfferRequest2))
                .isInstanceOf(ResponseStatusException.class);

        offerRequest = new OfferRequest();
        offerRequest.setLotId(null);
        offerRequest.setName(NON_EXISTS_OFFER_NAME);
        offerRequest.setPrice(NEW_PRICE_FOR_OFFER);
        OfferRequest finalOfferRequest1 = offerRequest;
        assertThatThrownBy(() -> offerService.add(finalOfferRequest1)).isInstanceOf(ResponseStatusException.class);

        offerRequest = new OfferRequest();
        offerRequest.setLotId(ON_EXISTS_LOT_ID);
        offerRequest.setName(NON_EXISTS_OFFER_NAME);
        offerRequest.setPrice(null);
        OfferRequest finalOfferRequest = offerRequest;
        assertThatThrownBy(() -> offerService.add(finalOfferRequest)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Transactional
    void testUpdate_OfferRequestIsValid() {

        //Проверка 1
        Map<String, Object> newAttributes = new HashMap<>();
        newAttributes.put("Fraction", "Horde");
        newAttributes.put("Condition", true);
        newAttributes.put("Value", 32F);

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setId(ON_EXISTS_OFFER_ID);
        offerRequest.setLotId(null);
        offerRequest.setName(NEW_NAME_FOR_OFFER);
        offerRequest.setPrice(NEW_PRICE_FOR_OFFER);
        offerRequest.setAvailability(null);
        offerRequest.setLongDescription("Product long description");
        offerRequest.setShortDescription("short description");
        offerRequest.setAttributes(newAttributes);

        ResponseEntity<OfferResponse> response = (ResponseEntity<OfferResponse>) offerService.update(offerRequest);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(NEW_NAME_FOR_OFFER);
        assertThat(response.getBody().getPrice()).isEqualTo(NEW_PRICE_FOR_OFFER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getLongDescription()).isEqualTo(offerRequest.getLongDescription());
        assertThat(response.getBody().getShortDescription()).isEqualTo(offerRequest.getShortDescription());
        assertThat(response.getBody().getAttributes().size()).isEqualTo(4);


        //Проверка 2
        offerRequest = new OfferRequest();
        offerRequest.setId(ON_EXISTS_OFFER_ID);
        offerRequest.setLotId(null);
        offerRequest.setName(null);
        offerRequest.setPrice(null);
        offerRequest.setAvailability(null);
        offerRequest.setLongDescription(null);
        offerRequest.setShortDescription(null);
        offerRequest.setAttributes(null);

        response = (ResponseEntity<OfferResponse>) offerService.update(offerRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);

    }

    @Test
    @Transactional
    void testUpdate_OfferRequestIsNotValid() {
        assertThatThrownBy(() -> offerService.update(null)).isInstanceOf(ResponseStatusException.class);

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setId(null);
        OfferRequest finalOfferRequest = offerRequest;
        assertThatThrownBy(() -> offerService.update(finalOfferRequest)).isInstanceOf(ResponseStatusException.class);

        offerRequest = new OfferRequest();
        offerRequest.setId(NON_EXISTS_OFFER_ID);
        OfferRequest finalOfferRequest1 = offerRequest;
        assertThatThrownBy(() -> offerService.update(finalOfferRequest1)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Transactional
    void testDelete_OfferRequestIsValid() {
        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setId(ON_EXISTS_OFFER_ID);

        ResponseEntity<?> response = offerService.delete(offerRequest);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThatThrownBy(() -> offerRepository.findById(ON_EXISTS_OFFER_ID).orElseThrow());
    }

    @Test
    @Transactional
    void testDelete_OfferRequestIsNotValid() {
        assertThatThrownBy(() -> offerService.delete(null)).isInstanceOf(ResponseStatusException.class);

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setId(null);
        OfferRequest finalOfferRequest = offerRequest;
        assertThatThrownBy(() -> offerService.delete(finalOfferRequest)).isInstanceOf(ResponseStatusException.class);

        offerRequest = new OfferRequest();
        offerRequest.setId(NON_EXISTS_OFFER_ID);
        OfferRequest finalOfferRequest1 = offerRequest;
        assertThatThrownBy(() -> offerService.delete(finalOfferRequest1)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Transactional
    void testGetAll_OfferRequestIsValid() {
        OfferRequest offerRequest1 = new OfferRequest();
        offerRequest1.setLotId(ON_EXISTS_LOT_ID);

        List<Offer> offer = offerRepository.findAllByLotId(ON_EXISTS_LOT_ID);

        assertThat(offer).isNotEmpty();

        ResponseEntity<List<OfferResponse>> response = (ResponseEntity<List<OfferResponse>>) offerService.getAll(offerRequest1);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().size()).isEqualTo(offer.size());
    }

    @Test
    @Transactional
    void testGetAll_OfferRequestIsNotValid() {
        assertThatThrownBy(() -> offerService.getAll(null)).isInstanceOf(ResponseStatusException.class);

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setLotId(null);
        OfferRequest finalOfferRequest = offerRequest;
        assertThatThrownBy(() -> offerService.getAll(finalOfferRequest)).isInstanceOf(ResponseStatusException.class);

        offerRequest = new OfferRequest();
        offerRequest.setLotId(NON_EXISTS_LOT_ID);
        OfferRequest finalOfferRequest1 = offerRequest;
        ResponseEntity<List<OfferResponse>> response = (ResponseEntity<List<OfferResponse>>) offerService.getAll(finalOfferRequest1);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    void testSearch_OfferRequestIsValid() {

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setId(ON_EXISTS_OFFER_ID);
        Pageable pageable = PageRequest.of(0, 10);

        ResponseEntity<Page<OfferResponse>> response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getId()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getName()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getPrice()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getAvailability()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getLongDescription()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getShortDescription()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getAttributes()).isNotNull();
        assertThat(response.getBody().getContent().get(0).getId()).isEqualTo(ON_EXISTS_OFFER_ID);


        for (int i = 0; i < 12; i++) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("Money", i);
            Offer offer = Offer.builder()
                    .name("Offer " + i)
                    .lot(lotRepository.findById(ON_EXISTS_LOT_ID).orElseThrow())
                    .attributes(attributes)
                    .build();
            offerRepository.save(offer);
        }

        offerRequest = new OfferRequest();
        offerRequest.setId(null);
        offerRequest.setName("Offer");

        pageable = PageRequest.of(1, 10);

        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(2);

        pageable = PageRequest.of(0, 5);

        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(5);

        offerRequest = new OfferRequest();

        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("Fraction", "Horde");
        attributes1.put("Condition", false);

        offerRequest.setId(null);
        offerRequest.setName(null);
        offerRequest.setPrice(null);
        offerRequest.setAvailability(null);
        offerRequest.setAttributes(attributes1);

        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);

        offerRequest = new OfferRequest();
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("Money", null);

        offerRequest.setId(null);
        offerRequest.setName(null);
        offerRequest.setPrice(null);
        offerRequest.setAvailability(null);
        offerRequest.setAttributes(attributes2);

        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(5);

        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(new OfferRequest(), pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(5);
    }

    @Test
    @Transactional
    void testSearch_OfferRequestIsNotValid() {
        Pageable pageable = PageRequest.of(0, 5);

        assertThatThrownBy(() -> offerService.search(null, pageable)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> offerService.search(new OfferRequest(), null)).isInstanceOf(ResponseStatusException.class);
        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setAttributes(Map.of("invalid", new Object()));

        ResponseEntity<Page<OfferResponse>> response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        offerRequest = new OfferRequest();
        offerRequest.setName(NON_EXISTS_OFFER_NAME);
        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        offerRequest = new OfferRequest();
        offerRequest.setId(NON_EXISTS_OFFER_ID);
        response = (ResponseEntity<Page<OfferResponse>>) offerService.search(offerRequest, pageable);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


}