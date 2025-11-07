package com.example.user_service.service;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.entity.PaymentCard;
import com.example.user_service.filter.UserFilter;
import com.example.user_service.mapper.PaymentCardMapper;
import com.example.user_service.repository.PaymentCardRepository;
import com.example.user_service.specification.PaymentCardSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    public PaymentCardDto create(PaymentCardDto paymentCardDto) {
        if (paymentCardRepository.countPaymentCardByUserId(paymentCardDto.getUserId()) >= 5)
            throw new IllegalArgumentException("One user must have no more than 5 cards");
        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardDto);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    public PaymentCardDto getById(UUID id){
        return paymentCardMapper.toDto(paymentCardRepository.findPaymentCardById(id));
    }
    public Page<PaymentCardDto> getAll(Pageable pageable, UserFilter userFilter){
        var spec = PaymentCardSpecification.cardUserNameSpecification(userFilter.getName())
                .and(PaymentCardSpecification.cardUserSurnameSpecification(userFilter.getSurname()));
        return paymentCardRepository.findAll(spec, pageable)
                .map(paymentCardMapper::toDto);
    }

    public List<PaymentCardDto> getAllByUserId(UUID userId){
        return paymentCardRepository.findAllByUserId(userId)
                .stream().map(paymentCardMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PaymentCardDto updateById(UUID id, PaymentCardDto paymentCardDto){
        PaymentCard paymentCard = paymentCardRepository.findPaymentCardById(id);
        paymentCard.setNumber(paymentCardDto.getNumber());
        paymentCard.setHolder(paymentCard.getHolder());
        paymentCard.setExpirationDate(paymentCardDto.getExpirationDate());
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    @Transactional
    public PaymentCardDto update(PaymentCardDto paymentCardDto){
        return updateById(paymentCardDto.getId(), paymentCardDto);
    }

    @Transactional
    public void activateById(UUID id){
        paymentCardRepository.activatePaymentCardById(id);
    }

    @Transactional
    public void deactivateById(UUID id){
        paymentCardRepository.deactivatePaymentCardById(id);
    }

    @Transactional
    public void deleteById(UUID id){
        paymentCardRepository.deleteById(id);
    }
}
