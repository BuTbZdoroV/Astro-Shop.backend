package org.productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.model.dto.response.lot.LotResponse;
import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Product;
import org.productservice.repository.LotRepository;
import org.productservice.repository.ProductRepository;
import org.productservice.service.admin.LotAdminService;
import org.productservice.service.user.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class LotServiceTest {

    public static final String ON_EXISTS_PRODUCT_NAME = "New Product 999";
    public static final String ON_EXISTS_LOT_NAME = "New Lot 999";
    public static final String NON_EXISTS_LOT_NAME = "New Lot 998";
    public static final long NON_EXISTS_PRODUCT_ID = 999L;

    @Autowired
    LotRepository lotRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    LotService lotService;

    @Autowired
    LotAdminService lotAdminService;

    Long ON_EXISTS_PRODUCT_ID;

    @BeforeEach
    @Transactional
    void setUp() {
        Product product = Product.builder()
                .name(ON_EXISTS_PRODUCT_NAME)
                .description("product")
                .build();

        Product savedProduct = productRepository.save(product);
        ON_EXISTS_PRODUCT_ID = savedProduct.getId();

        Lot lot = Lot.builder()
                .name(ON_EXISTS_LOT_NAME)
                .product(savedProduct)
                .build();

        lotRepository.save(lot);


    }

    @Test
    @Transactional
    void testCreate_LotRequestIsValid() {
        //Проверка 1
        LotRequest lotRequest = new LotRequest();
        lotRequest.setName(ON_EXISTS_LOT_NAME);
        lotRequest.setProductId(ON_EXISTS_PRODUCT_ID);

        ResponseEntity<LotResponse> response = (ResponseEntity<LotResponse>) lotAdminService.create(lotRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(ON_EXISTS_LOT_NAME);

        //Проверка 2
        LotRequest lotRequest2 = new LotRequest();
        lotRequest2.setName(NON_EXISTS_LOT_NAME);
        lotRequest2.setProductId(ON_EXISTS_PRODUCT_ID);

        ResponseEntity<LotResponse> response2 = (ResponseEntity<LotResponse>) lotAdminService.create(lotRequest2);

        assertThat(response2).isNotNull();
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response2.getBody()).isNotNull();
        assertThat(response2.getBody().getId()).isNotNull();
        assertThat(response2.getBody().getName()).isEqualTo(NON_EXISTS_LOT_NAME);
    }

    @Test
    @Transactional
    void testCreate_LotRequestIsNotValid() {
        //Проверка 1
        assertThatThrownBy(() -> lotAdminService.create(null))
                .isInstanceOf(ResponseStatusException.class);

        //Проверка 2
        LotRequest lotRequest1 = new LotRequest();
        lotRequest1.setName(ON_EXISTS_LOT_NAME);
        lotRequest1.setProductId(null);
        assertThatThrownBy(() -> lotAdminService.create(lotRequest1))
                .isInstanceOf(ResponseStatusException.class);

        //Проверка 3
        LotRequest lotRequest2 = new LotRequest();
        lotRequest2.setName(ON_EXISTS_LOT_NAME);
        lotRequest2.setProductId(NON_EXISTS_PRODUCT_ID);
        assertThatThrownBy(() -> lotAdminService.create(lotRequest2))
                .isInstanceOf(ResponseStatusException.class);

        //Проверка 4
        LotRequest lotRequest3 = new LotRequest();
        lotRequest3.setName("");
        lotRequest3.setProductId(ON_EXISTS_PRODUCT_ID);
        assertThatThrownBy(() -> lotAdminService.create(lotRequest3))
                .isInstanceOf(ResponseStatusException.class);
    }
}