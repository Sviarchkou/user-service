package com.example.user_service.controller;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.dto.UserDto;
import com.example.user_service.filter.UserFilter;
import com.example.user_service.service.PaymentCardService;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PaymentCardService paymentCardService;

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(@ModelAttribute UserFilter userFilter, @PageableDefault(size = 20, sort = "name") Pageable pageable){
        return ResponseEntity.ok(userService.getAll(pageable, userFilter));
    }

    @GetMapping("{id}/cards")
    public ResponseEntity<List<PaymentCardDto>> getUserPaymentCards(@PathVariable UUID id){
        return ResponseEntity.ok(paymentCardService.getAllByUserId(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateById(@PathVariable UUID id, @RequestBody @Validated(UserDto.UpdateGroup.class) UserDto userDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateById(id, userDto));
    }

    @PutMapping
    public ResponseEntity<UserDto> update(@RequestBody @Validated(UserDto.UpdateGroup.class) UserDto userDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.update(userDto));
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> changeActiveValue(@PathVariable UUID id, @RequestParam(required = true) boolean active){
        if (active) userService.activateById(id);
        else userService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
