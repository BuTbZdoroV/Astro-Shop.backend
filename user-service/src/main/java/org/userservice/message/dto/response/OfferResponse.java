package org.userservice.message.dto.response;

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
public class OfferResponse {
    Long id;
    String name;
    String shortDescription;
    String longDescription;
    Double price;
    Boolean active;
    Long userId;
    Date createdAt;
    Integer availability;
    Map<String, Object> attributes;
    LotResponse lot;
}