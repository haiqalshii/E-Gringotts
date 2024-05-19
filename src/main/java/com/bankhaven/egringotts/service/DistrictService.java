package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.DistrictDto;
import com.bankhaven.egringotts.repository.DistrictRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistrictService {

    private final DistrictRepository districtRepository;

    private final ModelMapper modelMapper;

    public DistrictService(DistrictRepository districtRepository, ModelMapper modelMapper) {
        this.districtRepository = districtRepository;
        this.modelMapper = modelMapper;
    }

    public List<DistrictDto> getAllDistricts() {

        return districtRepository
                .findAll()
                .stream()
                .map(district -> modelMapper.map(district, DistrictDto.class))
                .collect(Collectors.toList());
    }
}
