package org.productservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type")
@Data
@SuperBuilder
@AllArgsConstructor
public abstract class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String name;
    protected String description;
    protected Double price;
    protected Category category;
    protected Date createdAt;

    public Product() {
        this.category = getCategory();
    }

    public abstract Category getCategory();

    public enum Category {
        GAMES, SOFTWARE, SERVICE
    }
}


