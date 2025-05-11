package org.productservice.model.factory;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.ProductRequest;
import org.productservice.model.dto.request.productrequests.GameRequest;
import org.productservice.model.dto.request.productrequests.SoftwareRequest;
import org.productservice.model.entity.Product;
import org.productservice.model.factory.productfactory.GameFactory;
import org.productservice.model.factory.productfactory.SoftwareFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFactoryProvider {

    private final GameFactory gameFactory;
    private final SoftwareFactory softwareFactory;

    public Product createProduct(ProductRequest productRequest) {
        if (productRequest instanceof GameRequest gameRequest) {
            return gameFactory.createProduct(gameRequest);
        }
        if (productRequest instanceof SoftwareRequest softwareRequest) {
            return softwareFactory.createProduct(softwareRequest);
        }
        throw new IllegalArgumentException("unsupported type request" + productRequest.getClass().getName());
    }

}
