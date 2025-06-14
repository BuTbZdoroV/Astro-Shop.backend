package org.productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.productservice.model.dto.request.product.ProductRequest;
import org.productservice.model.dto.response.product.ProductResponse;
import org.productservice.model.entity.Product;
import org.productservice.repository.ProductRepository;
import org.productservice.service.admin.ProductAdminService;
import org.productservice.service.user.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
class ProductServiceTest {

    public static final String ON_EXISTS_PRODUCT_NAME = "NewProduct999";
    public static final String NON_EXISTS_PRODUCT_NAME = "NewProduct998";

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    ProductAdminService productAdminService;

    @BeforeEach
    @Transactional
    void setUp() {
        Product product = Product.builder()
                .name(ON_EXISTS_PRODUCT_NAME)
                .description("product")
                .build();

        productRepository.save(product);
    }

    @Test
    @Transactional
    void testCreate_ProductRequestIsValid() {
        //Проверка 1
        ProductRequest productRequest1 = new ProductRequest();
        productRequest1.setName(NON_EXISTS_PRODUCT_NAME);
        productRequest1.setDescription("product");

        ResponseEntity<ProductResponse> responseEntity1 = (ResponseEntity<ProductResponse>) productAdminService.create(productRequest1);

        assertThat(responseEntity1.getBody().getId()).isNotNull();
        assertThat(responseEntity1.getBody().getName()).isEqualTo(NON_EXISTS_PRODUCT_NAME);
        assertThat(responseEntity1.getBody().getDescription()).isEqualTo(productRequest1.getDescription());
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Проверка 2
        ProductRequest productRequest2 = new ProductRequest();
        productRequest2.setName(ON_EXISTS_PRODUCT_NAME);
        productRequest2.setDescription("product");

        ResponseEntity<ProductResponse> responseEntity2 = (ResponseEntity<ProductResponse>) productAdminService.create(productRequest2);

        assertThat(responseEntity2.getBody().getId()).isNotNull();
        assertThat(responseEntity2.getBody().getName()).isEqualTo(ON_EXISTS_PRODUCT_NAME);
        assertThat(responseEntity2.getBody().getDescription()).isEqualTo(productRequest2.getDescription());
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @Transactional
    void testCreate_ProductRequestIsNotValid() {
        //Проверка 1
        ProductRequest productRequest1 = new ProductRequest();
        productRequest1.setName("");
        productRequest1.setDescription("product");

        assertThatThrownBy(() -> productAdminService.create(productRequest1))
                .isInstanceOf(ResponseStatusException.class);

        //Проверка 2
        assertThatThrownBy(() -> productAdminService.create(null))
                .isInstanceOf(ResponseStatusException.class);
    }

}