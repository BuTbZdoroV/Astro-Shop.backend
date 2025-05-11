package org.productservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.productservice.model.dto.response.productresponse.GameResponse;
import org.productservice.model.dto.response.productresponse.SoftwareResponse;
import org.productservice.model.entity.Product;
import org.productservice.model.entity.products.Game;
import org.productservice.model.entity.products.Software;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class ProductResponse {
    Long id;
    String name;
    String description;
    Date createdAt;
    Product.Category category;

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (product instanceof Game game) {
            return GameResponse.builder()
                    .id(game.getId())
                    .name(game.getName())
                    .description(game.getDescription())
                    .createdAt(game.getCreatedAt())
                    .category(game.getCategory())
                    .genre(game.getGenre())
                    .platform(game.getPlatform())
                    .minAge(game.getMinAge())
                    .build();
        }

        if (product instanceof Software software) {
            return SoftwareResponse.builder()
                    .id(software.getId())
                    .name(software.getName())
                    .description(software.getDescription())
                    .createdAt(software.getCreatedAt())
                    .category(software.getCategory())
                    .licenseType(software.getLicenseType())
                    .version(software.getVersion())
                    .build();
        }

        throw new IllegalArgumentException("Unsupported product type: " + product.getClass().getName());
    }
}