package org.productservice.model.entity.products;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.productservice.model.entity.Product;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SOFTWARE")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Software extends Product {

    private String licenseType;
    private String version;

    @Override
    public Category getCategory() {
        return Category.SOFTWARE;
    }
}
