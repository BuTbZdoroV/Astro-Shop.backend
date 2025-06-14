package org.productservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.productservice.model.dto.request.lot.LotRequest;
import org.productservice.service.admin.LotAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lots/admin")
public class LotAdminController {
    private final LotAdminService lotAdminService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LotRequest request) {
        return lotAdminService.create(request);
    }
}
