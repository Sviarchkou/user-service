package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.User;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.filter.UserFilter;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.specification.UserSpecification;
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

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;

    @CachePut(value = "users", key = "#result.id")
    public UserDto create(UserDto userDto){
        User user = userMapper.toEntity(userDto);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UserDto> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }
        user = userRepository.save(user);
        user.setUpdatedAt(null);
        return userMapper.toDto(user);
    }

    @Cacheable(value = "users", key = "#id")
    public UserDto getById(UUID id){
        return userMapper.toDto(userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id))));
    }

    public Page<UserDto> getAll(Pageable pageable, String name, String surname){
        return userRepository.findAll(UserSpecification.userNameSpecification(name)
                .and(UserSpecification.userSurnameSpecification(surname)), pageable)
                .map(userMapper::toDto);
    }

    public Page<UserDto> getAll(Pageable pageable, UserFilter userFilter){
        return userRepository.findAll(UserSpecification.userFilterSpecification(userFilter), pageable)
                .map(userMapper::toDto);
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserDto updateById(UUID id, UserDto userDto){
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, UserDto.UpdateGroup.class);
        if (!violations.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UserDto> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }

        User user = userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id)));
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setBirthDate(userDto.getBirthDate());
        return userMapper.toDto(userRepository.saveAndFlush(user));
    }

    @CachePut(value = "users", key = "#userDto.id")
    @Transactional
    public UserDto update(UserDto userDto){
        return updateById(userDto.getId(), userDto);
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserDto activateById(UUID id){
        try {
            userRepository.activateUserById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Can not activate user with id: " + id, e);
        }
        return userMapper.toDto(userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id))));
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserDto deactivateById(UUID id){
        try {
            userRepository.deactivateUserById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Can not deactivate user with id: " + id, e);
        }
        return userMapper.toDto(userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id))));
    }

    @CacheEvict(value = "users", key = "#id", beforeInvocation = true)
    @Transactional
    public void deleteById(UUID id){
        userRepository.deleteById(id);
    }

}
