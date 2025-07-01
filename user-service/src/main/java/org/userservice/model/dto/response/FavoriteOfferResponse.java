package org.userservice.model.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteOfferResponse {
    Long id;
    Long userId;
    Long offerId;
    LocalDateTime createdAt;
}
