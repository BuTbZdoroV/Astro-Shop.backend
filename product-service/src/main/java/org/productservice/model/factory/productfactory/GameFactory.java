package org.productservice.model.factory.productfactory;

import org.productservice.model.dto.request.ProductRequest;
import org.productservice.model.dto.request.productrequests.GameRequest;
import org.productservice.model.entity.Product;
import org.productservice.model.entity.products.Game;
import org.productservice.model.factory.ProductFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GameFactory implements ProductFactory {
    @Override
    public Product createProduct(ProductRequest request) {
        if (!(request instanceof GameRequest gameRequest)) {
            throw new IllegalArgumentException("GameFactory supports only GameRequest");
        }

        return Game.builder()
                .name(gameRequest.getName())
                .description(gameRequest.getDescription())
                .category(Product.Category.GAMES)
                .createdAt(new Date())
                .platform(gameRequest.getPlatform())
                .genre(gameRequest.getGenre())
                .minAge(gameRequest.getMinAge())
                .build();
    }
}
