package com.bankhaven.egringotts.dto.request.auth;

import com.bankhaven.egringotts.dto.model.CardDto;
import com.bankhaven.egringotts.model.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RegisterUserRequestDto {

    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Email field cannot be empty.")
    private String email;

    @NotBlank(message = "Password field cannot be empty.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @NotBlank(message = "First name field cannot be empty.")
    private String firstName;

    @NotBlank(message = "Last name field cannot be empty.")
    private String lastName;

    @Pattern(regexp = "^\\d{10,13}$", message = "Phone number must be between 10 and 13 digits.")
    private String phoneNumber;

    @NotBlank(message = "Address field cannot be empty.")
    @Size(max = 250, message = "Address field cannot be longer than 250 characters.")
    private String plainAddress;

    @NotBlank(message = "District field cannot be empty.")
    private String district;

    @NotBlank(message = "Place field cannot be empty.")
    private String place;

    @Past(message = "Date of birth cannot be in the future.")
    private LocalDate dateOfBirth;

    private UserRole role;

    private List<CardDto> cards;

    private String combinedAddress;

    private String displayCards;

    // Method to combine the address details
    public String combineAddress() {
        return this.combinedAddress = this.plainAddress + ", " + this.place + ", " + this.district;
    }

    public String displayCards() {
        StringBuilder cardsDetails = new StringBuilder();
        for (CardDto card : cards) {
            cardsDetails.append(card.getCardNumber()).append(", ")
                    .append(card.getCvv()).append(", ")
                    .append(card.getExpirationDate()).append("\n");
        }
        return cardsDetails.toString();
    }
}

