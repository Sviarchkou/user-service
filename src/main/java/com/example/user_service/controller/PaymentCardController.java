package com.example.user_service.controller;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.filter.SpecializationFilter;
import com.example.user_service.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.hasPaymentCard(authentication, #id)")
    @GetMapping("{id}")
    public ResponseEntity<PaymentCardDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(paymentCardService.getById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<PaymentCardDto>> getAll(@ModelAttribute SpecializationFilter specializationFilter, @PageableDefault(size = 30, sort = "number") Pageable pageable){
        return ResponseEntity.ok(paymentCardService.getAll(pageable, specializationFilter));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<PaymentCardDto> create(@RequestBody @Valid PaymentCardDto paymentCardDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.create(paymentCardDto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.hasPaymentCard(authentication, #id)")
    @PutMapping("{id}")
    public ResponseEntity<PaymentCardDto> updateById(@PathVariable UUID id, @RequestBody @Valid PaymentCardDto paymentCardDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentCardService.updateById(id, paymentCardDto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("{id}/activate")
    public ResponseEntity<PaymentCardDto> activateById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentCardService.activateById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("{id}/deactivate")
    public ResponseEntity<PaymentCardDto> deactivateById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentCardService.deactivateById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.hasPaymentCard(authentication, #id)")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id){
        paymentCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
