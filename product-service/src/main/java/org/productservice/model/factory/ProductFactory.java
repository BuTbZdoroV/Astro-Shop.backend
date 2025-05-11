package org.productservice.model.factory;

import org.productservice.model.dto.request.ProductRequest;
import org.productservice.model.entity.Product;

public interface ProductFactory {
    /**
     * Создает продукт на основе запроса
     * @param request Запрос (GameRequest, SoftwareRequest и т. д.)
     * @return Созданный продукт (Game, Software и т. д.)
     * @throws IllegalArgumentException Если тип продукта не поддерживается
     */
    Product createProduct(ProductRequest request);
}