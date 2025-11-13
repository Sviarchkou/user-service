package com.example.user_service.mapper;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "userId", target = "user.id")
    PaymentCard toEntity(PaymentCardDto paymentCardDto);

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto toDto(PaymentCard paymentCard);

}
