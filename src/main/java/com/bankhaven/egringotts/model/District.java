package com.bankhaven.egringotts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "districts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class District {

    @Id
    private int id;

    private String title;
}
