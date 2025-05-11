package org.productservice.model.dto.request.productrequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.productservice.model.dto.request.ProductRequest;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareRequest extends ProductRequest {
    String licenseType;
    String version;
}
