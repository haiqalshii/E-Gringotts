package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.PlaceDto;
import com.bankhaven.egringotts.repository.PlaceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final ModelMapper modelMapper;

    public PlaceService(PlaceRepository placeRepository, ModelMapper modelMapper) {
        this.placeRepository = placeRepository;
        this.modelMapper = modelMapper;
    }

    public List<PlaceDto> getAllPlacesByDistrictId(int districtId) {
        return placeRepository
                .findByDistrictId(districtId)
                .stream()
                .map(place -> modelMapper.map(place, PlaceDto.class))
                .collect(Collectors.toList());
    }
}
