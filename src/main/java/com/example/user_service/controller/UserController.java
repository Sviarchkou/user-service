package com.example.user_service.controller;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.dto.UserDto;
import com.example.user_service.filter.SpecializationFilter;
import com.example.user_service.request.UserIdListRequest;
import com.example.user_service.service.PaymentCardService;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.hasUserId(authentication, #id)")
    @GetMapping("{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAll(@ModelAttribute SpecializationFilter specializationFilter, @PageableDefault(size = 20, sort = "name") Pageable pageable){
        return ResponseEntity.ok(userService.getAll(pageable, specializationFilter));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("in")
    public ResponseEntity<List<UserDto>> getAllByUserIdList(@Valid UserIdListRequest request){
        return ResponseEntity.ok(userService.getAllByUserIdList(request));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.hasPaymentCard(authentication, #id)")
    @GetMapping("{id}/cards")
    public ResponseEntity<List<PaymentCardDto>> getUserPaymentCards(@PathVariable UUID id){
        return ResponseEntity.ok(paymentCardService.getAllByUserId(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.hasUserId(authentication, #id)")
    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateById(@PathVariable UUID id, @RequestBody @Validated(UserDto.UpdateGroup.class) UserDto userDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateById(id, userDto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') and !@userSecurity.hasUserId(authentication, #id)")
    @PutMapping("{id}/activate")
    public ResponseEntity<UserDto> activateById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.activateById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') and !@userSecurity.hasUserId(authentication, #id)")
    @PutMapping("{id}/deactivate")
    public ResponseEntity<UserDto> deactivateById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.deactivateById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') and !@userSecurity.hasUserId(authentication, #id)")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
