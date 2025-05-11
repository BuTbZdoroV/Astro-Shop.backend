package org.productservice.service;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.ProductFilterRequest;
import org.productservice.model.dto.request.ProductRequest;
import org.productservice.model.dto.request.productrequests.GameRequest;
import org.productservice.model.dto.response.ProductResponse;
import org.productservice.model.dto.response.productresponse.GameResponse;
import org.productservice.model.dto.specification.ProductSpecifications;
import org.productservice.model.entity.Product;
import org.productservice.model.entity.products.Game;
import org.productservice.model.factory.ProductFactoryProvider;
import org.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductFactoryProvider productFactoryProvider;

    @Transactional
    public ResponseEntity<?> addProduct(ProductRequest productRequest) {
        try {
            Product product = productFactoryProvider.createProduct(productRequest);
            Product savedProduct = productRepository.save(product);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ProductResponse.toResponse(savedProduct));

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productRepository.delete(product);
        return new ResponseEntity<>("Product delete by ID: " + id, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(ProductResponse.toResponse(product));
    }


    /**
     * Поиск продуктов с динамической фильтрацией и пагинацией.
     *
     * @param filter   - DTO с параметрами фильтрации, Если поле null, оно не учитывается в фильтрации.
     * @param pageable Параметры пагинации и сортировки (номер страницы, размер, сортировка).
     * @return Страница (Page) с отфильтрованными продуктами в формате {@link ProductResponse}.
     * Возвращает HTTP 200 с данными.
     * @throws ResponseStatusException HTTP 204 (No Content), если продукты не найдены.
     * @example GET /api/products?name=Phone&page=0&size=10
     * @see ProductSpecifications
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> search(ProductFilterRequest filter, Pageable pageable) {
        try {
            Specification<Product> specification = Specification.where(null);

            if (filter.getName() != null) {
                specification = specification.and(ProductSpecifications.nameLike(filter.getName()));
            }

            if (filter.getCategory() != null) {
                specification = specification.and(ProductSpecifications.categoryLike(filter.getCategory()));
            }

            Page<Product> productsPage = productRepository.findAll(specification, pageable);
            Page<ProductResponse> responsePage = productsPage.map(ProductResponse::toResponse);

            if (responsePage.isEmpty()) {
                logger.warn("No products found with filter: {}", filter);
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(responsePage);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
