package org.productservice.model.dto.request;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.productservice.model.dto.request.productrequests.GameRequest;
import org.productservice.model.dto.request.productrequests.SoftwareRequest;
import org.productservice.model.entity.Product;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "category",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameRequest.class, name = "GAMES"),
        @JsonSubTypes.Type(value = SoftwareRequest.class, name = "SOFTWARE")
})
public abstract class ProductRequest {
       @NotBlank
       @Size(min = 2, max = 50)
       String name;
       String description;
       @NotBlank
       Product.Category category;
       protected Double price;
       protected Date createdAt;
}
