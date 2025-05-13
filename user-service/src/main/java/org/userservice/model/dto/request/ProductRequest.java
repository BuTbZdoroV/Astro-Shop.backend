package org.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private Double price;
    private String description;
    private Category category;

    // Геттеры и сеттеры
    public enum Category {
        GAMES, SOFTWARE, SERVICE
    }
}