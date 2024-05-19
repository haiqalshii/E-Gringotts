package com.bankhaven.egringotts.config;

import com.bankhaven.egringotts.dto.model.AddressDto;
import com.bankhaven.egringotts.model.Address;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(Address.class, AddressDto.class);

//        modelMapper.createTypeMap(User.class, UserDto.class)
//                .addMappings(mapper -> mapper.map(User::getAddress, UserDto::setAddress));
        return modelMapper;
    }

}
