package com.example.user_service.controller;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.filter.UserFilter;
import com.example.user_service.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @GetMapping("{id}")
    public ResponseEntity<PaymentCardDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(paymentCardService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDto>> getAll(@ModelAttribute UserFilter userFilter, @PageableDefault(size = 30, sort = "number") Pageable pageable){
        return ResponseEntity.ok(paymentCardService.getAll(pageable, userFilter));
    }

    @PostMapping
    public ResponseEntity<PaymentCardDto> create(@RequestBody @Valid PaymentCardDto paymentCardDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.create(paymentCardDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<PaymentCardDto> updateById(@PathVariable UUID id, @RequestBody @Valid PaymentCardDto paymentCardDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentCardService.updateById(id, paymentCardDto));
    }

    @PutMapping
    public ResponseEntity<PaymentCardDto> update(@RequestBody @Valid PaymentCardDto paymentCardDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentCardService.update(paymentCardDto));
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> changeActiveValue(@PathVariable UUID id, @RequestParam(required = true) boolean active){
        if (active) paymentCardService.activateById(id);
        else paymentCardService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id){
        paymentCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
