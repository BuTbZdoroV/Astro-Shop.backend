package org.productservice.model.factory.productfactory;

import org.productservice.model.dto.request.ProductRequest;
import org.productservice.model.dto.request.productrequests.SoftwareRequest;
import org.productservice.model.entity.Product;
import org.productservice.model.entity.products.Software;
import org.productservice.model.factory.ProductFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SoftwareFactory implements ProductFactory {
    @Override
    public Product createProduct(ProductRequest request) {
        if (!(request instanceof SoftwareRequest softwareRequest)) throw new IllegalArgumentException("Request must be of type SoftwareRequest");

        return Software.builder()
                .name(softwareRequest.getName())
                .description(softwareRequest.getDescription())
                .createdAt(new Date())
                .price(softwareRequest.getPrice())
                .category(softwareRequest.getCategory())
                .version(softwareRequest.getVersion())
                .licenseType(softwareRequest.getLicenseType())
                .build();
    }
}
