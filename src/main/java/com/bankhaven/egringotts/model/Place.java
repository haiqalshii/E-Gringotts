package com.bankhaven.egringotts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "places")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    @Id
    private Integer id;

    private String title;

    @JoinColumn(name = "district_id")
    private int districtId;
}
