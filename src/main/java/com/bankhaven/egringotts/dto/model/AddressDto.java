package com.bankhaven.egringotts.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String id;
    private String plainAddress;
    private DistrictDto district;
    private PlaceDto place;

}
