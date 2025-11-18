package com.example.user_service.service;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.entity.PaymentCard;
import com.example.user_service.exception.CardLimitExceededException;
import com.example.user_service.exception.EmailAlreadyExistsException;
import com.example.user_service.exception.PaymentCardNotFoundException;
import com.example.user_service.filter.SpecializationFilter;
import com.example.user_service.mapper.PaymentCardMapper;
import com.example.user_service.repository.PaymentCardRepository;
import com.example.user_service.specification.PaymentCardSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final int PAYMENT_CARD_LIMIT = 5;

    @CachePut(value = "paymentCards", key = "#result.id")
    public PaymentCardDto create(PaymentCardDto paymentCardDto) {
        if (paymentCardRepository.countPaymentCardByUserId(paymentCardDto.getUserId()) >= PAYMENT_CARD_LIMIT) {
            throw new CardLimitExceededException("One user must have no more than %d cards".formatted(PAYMENT_CARD_LIMIT));
        }
        if (paymentCardRepository.existsByNumber(paymentCardDto.getNumber())) {
            throw new EmailAlreadyExistsException("Payment card with number: %s already exists".formatted(paymentCardDto.getNumber()));
        }
        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardDto);
        paymentCard = paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(paymentCard);
    }

    @Cacheable(value = "paymentCards", key = "#id")
    public PaymentCardDto getById(UUID id){
        return paymentCardMapper.toDto(paymentCardRepository.findPaymentCardById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id))));
    }

    public Page<PaymentCardDto> getAll(Pageable pageable, SpecializationFilter specializationFilter){
        var spec = PaymentCardSpecification.cardUserFilterSpecification(specializationFilter);
        return paymentCardRepository.findAll(spec, pageable)
                .map(paymentCardMapper::toDto);
    }

    public List<PaymentCardDto> getAllByUserId(UUID userId){
        return paymentCardRepository.findAllByUserId(userId)
                .stream().map(paymentCardMapper::toDto).collect(Collectors.toList());
    }

    @CachePut(value = "paymentCards", key = "#id")
    @Transactional
    public PaymentCardDto updateById(UUID id, PaymentCardDto paymentCardDto){
        PaymentCard paymentCard = paymentCardRepository.findPaymentCardById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id)));
        if (paymentCardRepository.existsByNumber(paymentCardDto.getNumber())) {
            throw new EmailAlreadyExistsException("Payment card with number: %s already exists".formatted(paymentCardDto.getNumber()));
        }
        paymentCard.setNumber(paymentCardDto.getNumber());
        paymentCard.setHolder(paymentCardDto.getHolder());
        paymentCard.setExpirationDate(paymentCardDto.getExpirationDate());
        return paymentCardMapper.toDto(paymentCardRepository.saveAndFlush(paymentCard));
    }

    @CachePut(value = "paymentCards", key = "#paymentCardDto.id")
    @Transactional
    public PaymentCardDto update(PaymentCardDto paymentCardDto){
        return updateById(paymentCardDto.getId(), paymentCardDto);
    }

    @CachePut(value = "paymentCards", key = "#id")
    @Transactional
    public PaymentCardDto activateById(UUID id){
        PaymentCard paymentCard = paymentCardRepository.findById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id)));
        paymentCard.setActive(true);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    @CachePut(value = "paymentCards", key = "#id")
    @Transactional
    public PaymentCardDto deactivateById(UUID id){
        PaymentCard paymentCard = paymentCardRepository.findById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id)));
        paymentCard.setActive(false);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    @CacheEvict(value = "paymentCards", key = "#id")
    @Transactional
    public void deleteById(UUID id){
        paymentCardRepository.deleteById(id);
    }
}
