package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    Optional<District> findByTitle(String title);
}
