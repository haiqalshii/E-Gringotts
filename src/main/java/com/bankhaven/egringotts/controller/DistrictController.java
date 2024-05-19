package com.bankhaven.egringotts.controller;

import com.bankhaven.egringotts.dto.model.DistrictDto;
import com.bankhaven.egringotts.service.DistrictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/districts")
public class DistrictController {

    private final DistrictService districtService;

    public DistrictController(DistrictService districtService) {
        this.districtService = districtService;
    }

    @GetMapping
    public ResponseEntity<List<DistrictDto>> getAllDistricts() {

        return ResponseEntity.ok(districtService.getAllDistricts());

    }

}
