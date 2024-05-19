package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.AddressDto;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.auth.LoginUserRequestDto;
import com.bankhaven.egringotts.dto.request.auth.RegisterUserRequestDto;
import com.bankhaven.egringotts.exception.DistrictNotFoundException;
import com.bankhaven.egringotts.exception.EmailAlreadyInUseException;
import com.bankhaven.egringotts.exception.PlaceNotFoundException;
import com.bankhaven.egringotts.model.*;
import com.bankhaven.egringotts.repository.AddressRepository;
import com.bankhaven.egringotts.repository.DistrictRepository;
import com.bankhaven.egringotts.repository.PlaceRepository;
import com.bankhaven.egringotts.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final DistrictRepository districtRepository;
    private final PlaceRepository placeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public AuthenticationService(UserRepository userRepository, AddressRepository addressRepository, DistrictRepository districtRepository, PlaceRepository placeRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.districtRepository = districtRepository;
        this.placeRepository = placeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    public String register(RegisterUserRequestDto registerUserDto) {

        Optional<User> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());

        if (optionalUser.isPresent()) throw new EmailAlreadyInUseException("This email already in use.");

        User user = User.builder()
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .dateOfBirth(registerUserDto.getDateOfBirth())
                .email(registerUserDto.getEmail())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .phoneNumber(registerUserDto.getPhoneNumber())
                .role(registerUserDto.getRole())
                .build();

        Optional<District> district = districtRepository.findByTitle(registerUserDto.getDistrict());

        if(district.isEmpty()) throw new DistrictNotFoundException("District not found.");

        Optional<Place> place = placeRepository
                .findByTitleAndDistrictId(registerUserDto.getPlace(), district.get().getId());

        if(place.isEmpty())
            throw new PlaceNotFoundException("Place " + registerUserDto.getPlace() +  " not found for the district : " + district.get().getTitle());

        User savedUser = userRepository.save(user);

        Address address = Address.builder()
                .plainAddress(registerUserDto.getPlainAddress())
                .district(district.get())
                .place(place.get())
                .userId(savedUser.getId())
                .build();

        List<Cards> cardEntities = registerUserDto.getCards().stream()
                .map(cardDto -> Cards.builder()
                        .cardNumber(cardDto.getCardNumber())
                        .cvv(cardDto.getCvv())
                        .expirationDate(cardDto.getExpirationDate())
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());

        savedUser.setCards(cardEntities);
        userRepository.save(savedUser);

        Address savedAddress = addressRepository.save(address);

        UserDto userDto = modelMapper.map(savedUser, UserDto.class);

        userDto.setAddress(modelMapper.map(savedAddress, AddressDto.class));



        String userDetails = String.format("\nName: %s %s\nEmail: %s\nPhone: %s\nDate of Birth: %s\nAddress: %s\nCards: %s",
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail(), savedUser.getPhoneNumber(), savedUser.getDateOfBirth(), registerUserDto.combineAddress(), registerUserDto.displayCards());

        emailService.sendUserCreationMessage(savedUser.getEmail(), userDetails);

        return "User created successfully";
    }

    public User login(LoginUserRequestDto loginUserDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword()));
        return userRepository.findByEmail(loginUserDto.getEmail()).orElseThrow();
    }

}
