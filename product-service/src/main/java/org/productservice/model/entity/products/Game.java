package org.productservice.model.entity.products;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.productservice.model.entity.Product;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("GAME")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Game extends Product {
    private String genre;
    private String platform;
    private Integer minAge;

    @Override
    public Category getCategory() {
        return Category.GAMES;
    }
}
