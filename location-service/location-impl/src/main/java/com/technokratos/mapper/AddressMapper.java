package com.technokratos.mapper;

import com.technokratos.dto.AddressDto;
import com.technokratos.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDto dto);
    AddressDto toDto(Address entity);
}
