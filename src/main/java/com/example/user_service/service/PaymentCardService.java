package com.example.user_service.service;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.entity.PaymentCard;
import com.example.user_service.exception.PaymentCardNotFoundException;
import com.example.user_service.filter.UserFilter;
import com.example.user_service.mapper.PaymentCardMapper;
import com.example.user_service.repository.PaymentCardRepository;
import com.example.user_service.specification.PaymentCardSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final Validator validator;

    @CachePut(value = "paymentCards", key = "#result.id")
    public PaymentCardDto create(PaymentCardDto paymentCardDto) {
        Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);
        if (!violations.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<PaymentCardDto> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }

        if (paymentCardRepository.countPaymentCardByUserId(paymentCardDto.getUserId()) >= 5) {
            throw new IllegalArgumentException("One user must have no more than 5 cards");
        }
        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardDto);
        paymentCard = paymentCardRepository.save(paymentCard);
        paymentCard.setUpdatedAt(null);
        return paymentCardMapper.toDto(paymentCard);
    }

    @Cacheable(value = "paymentCards", key = "#id")
    public PaymentCardDto getById(UUID id){
        return paymentCardMapper.toDto(paymentCardRepository.findPaymentCardById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id))));
    }

    public Page<PaymentCardDto> getAll(Pageable pageable, UserFilter userFilter){
        var spec = PaymentCardSpecification.cardUserFilterSpecification(userFilter);
        return paymentCardRepository.findAll(spec, pageable)
                .map(paymentCardMapper::toDto);
    }

    @CachePut(value = "userPaymentCards", key = "#userId")
    public List<PaymentCardDto> getAllByUserId(UUID userId){
        return paymentCardRepository.findAllByUserId(userId)
                .stream().map(paymentCardMapper::toDto).collect(Collectors.toList());
    }

    @CachePut(value = "paymentCards", key = "#id")
    @Transactional
    public PaymentCardDto updateById(UUID id, PaymentCardDto paymentCardDto){
        Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto, PaymentCardDto.UpdateGroup.class);
        if (!violations.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<PaymentCardDto> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }

        PaymentCard paymentCard = paymentCardRepository.findPaymentCardById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id)));
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
        try{
            paymentCardRepository.activatePaymentCardById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Can not activate payment card with id: " + id, e);
        }
        return paymentCardMapper.toDto(paymentCardRepository.findPaymentCardById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id))));
    }

    @CachePut(value = "paymentCards", key = "#id")
    @Transactional
    public PaymentCardDto deactivateById(UUID id){
        try{
            paymentCardRepository.deactivatePaymentCardById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Can not deactivate payment card with id: " + id, e);
        }
        return paymentCardMapper.toDto(paymentCardRepository.findPaymentCardById(id).orElseThrow(
                () -> new PaymentCardNotFoundException("Payment card with id: %s is not found".formatted(id))));
    }

    @CacheEvict(value = "paymentCards", key = "#id", beforeInvocation = true)
    @Transactional
    public void deleteById(UUID id){
        paymentCardRepository.deleteById(id);
    }
}
