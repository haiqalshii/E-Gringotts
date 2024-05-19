package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    List<Place> findByDistrictId(int districtId);

    Optional<Place> findByTitleAndDistrictId(String title, int districtId);
}
