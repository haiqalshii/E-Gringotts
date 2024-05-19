package com.bankhaven.egringotts.repository;

import com.bankhaven.egringotts.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    Address findByUserId(String userId);

}
