package org.productservice.model.dto.request.offer;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {
       Long id;
       @Size(min = 2, max = 50)
       String name;
       @Size(min = 0, max = 50)
       String shortDescription;
       @Size(min = 0, max = 1000)
       String longDescription;
       Long lotId;
       Long userId;
       Double price;
       Integer availability;
       Boolean active;
       Map<String, Object> attributes;


}
