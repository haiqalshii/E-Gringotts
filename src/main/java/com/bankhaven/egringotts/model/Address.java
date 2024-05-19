package com.bankhaven.egringotts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String plainAddress;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @JoinColumn(name = "user_id")
    private String userId;

    public Address(String userId, String plainAddress, District district, Place place) {
        this.userId = userId;
        this.plainAddress = plainAddress;
        this.district = district;
        this.place = place;
    }
}
