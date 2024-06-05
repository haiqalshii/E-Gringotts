package com.bankhaven.egringotts.service;

import com.bankhaven.egringotts.dto.model.AddressDto;
import com.bankhaven.egringotts.dto.model.UserDto;
import com.bankhaven.egringotts.dto.request.user.UpdateUserRequestDto;
import com.bankhaven.egringotts.exception.DistrictNotFoundException;
import com.bankhaven.egringotts.exception.PlaceNotFoundException;
import com.bankhaven.egringotts.exception.UserNotFoundException;
import com.bankhaven.egringotts.model.*;
import com.bankhaven.egringotts.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AccountRepository accountRepository;
    private final DistrictRepository districtRepository;
    private final PlaceRepository placeRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, AddressRepository addressRepository, AccountRepository accountRepository, DistrictRepository districtRepository, PlaceRepository placeRepository, TransactionRepository transactionRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.accountRepository = accountRepository;
        this.districtRepository = districtRepository;
        this.placeRepository = placeRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    public List<UserDto> getAllUsersGoblin() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            // Fetch the address associated with the user
            Address address = addressRepository.findByUserId(user.getId());

            // Map the address to an AddressDto
            AddressDto addressDto = modelMapper.map(address, AddressDto.class);

            // Map the user to a UserDto including the address
            UserDto userDto = modelMapper.map(user, UserDto.class);
            userDto.setAddress(addressDto);

            return userDto;
        }).collect(Collectors.toList());
    }

    public String updateUserDetails(UpdateUserRequestDto updateUserRequest) {
        User user = userRepository.findById(updateUserRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setEmail(updateUserRequest.getEmail());
        user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        user.setDateOfBirth(updateUserRequest.getDateOfBirth());

        if (updateUserRequest.getPlainAddress() != null || updateUserRequest.getDistrict() != null || updateUserRequest.getPlace() != null) {
            Address address = addressRepository.findByUserId(updateUserRequest.getUserId());
            if (address != null) {
                address.setPlainAddress(updateUserRequest.getPlainAddress());
                if (updateUserRequest.getDistrict() != null) {
                    District district = districtRepository.findByTitle(updateUserRequest.getDistrict())
                            .orElseThrow(() -> new DistrictNotFoundException("District not found"));
                    address.setDistrict(district);
                }
                if (updateUserRequest.getPlace() != null) {
                    Place place = placeRepository.findByTitleAndDistrictId(updateUserRequest.getPlace(), address.getDistrict().getId())
                            .orElseThrow(() -> new PlaceNotFoundException("Place not found for the district: " + address.getDistrict().getTitle()));
                    address.setPlace(place);
                }
                addressRepository.save(address);
            }
        }

        userRepository.save(user);

        return "User updated successfully";
    }


    public UserDto getCurrentUser(User user) {

        Address address = addressRepository.findByUserId(user.getId());
        AddressDto addressDto = modelMapper.map(address, AddressDto.class);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setAddress(addressDto);

        return userDto;
    }

    public String deleteUserById(String id) {
        // Fetch all accounts of the user
        List<Account> accounts = accountRepository.findAllByUserId(id);

        // For each account, delete associated transactions and then the account
        for (Account account : accounts) {
            List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountId(id);
            transactionRepository.deleteAll(transactions);
            accountRepository.delete(account);
        }

        // Finally, delete the user
        userRepository.deleteById(id);

        return "User deleted successfully";
    }


}
