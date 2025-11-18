package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.User;
import com.example.user_service.exception.EmailAlreadyExistsException;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.filter.SpecializationFilter;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @CachePut(value = "users", key = "#result.id")
    public UserDto create(UserDto userDto){
        if (userRepository.existsUserByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("User with email: %s already exists".formatted(userDto.getEmail()));
        }
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
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

    public Page<UserDto> getAll(Pageable pageable, SpecializationFilter specializationFilter){
        return userRepository.findAll(UserSpecification.userFilterSpecification(specializationFilter), pageable)
                .map(userMapper::toDto);
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserDto updateById(UUID id, UserDto userDto){
        User user = userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id)));
        if (userRepository.existsUserByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("User with email: %s already exists".formatted(userDto.getEmail()));
        }
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
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id)));
        user.setActive(true);
        return userMapper.toDto(userRepository.save(user));
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserDto deactivateById(UUID id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s is not found".formatted(id)));
        user.setActive(false);
        return userMapper.toDto(userRepository.save(user));
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteById(UUID id){
        userRepository.deleteById(id);
    }

}
