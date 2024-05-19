package com.bankhaven.egringotts.dto.request.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateUserRequestDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDateTime dateOfBirth;
    private String plainAddress;
    private String district;
    private String place;
}
