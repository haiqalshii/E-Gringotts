package com.bankhaven.egringotts.controller;

import com.bankhaven.egringotts.dto.model.PlaceDto;
import com.bankhaven.egringotts.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {

    private final PlaceService placesService;

    public PlaceController(PlaceService placesService) {
        this.placesService = placesService;
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<List<PlaceDto>> getAllPlacesByCityId(@PathVariable int cityId) {
        return ResponseEntity.ok(placesService.getAllPlacesByDistrictId(cityId));
    }

}
