package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.User;
import com.example.user_service.filter.UserFilter;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto create(UserDto userDto){
        User user = userMapper.toEntity(userDto);
        if (Objects.isNull(user.getName()))
            throw new IllegalArgumentException("User name must be non null on creation");
        if (Objects.isNull(user.getSurname()))
            throw new IllegalArgumentException("User surname must be non null on creation");
        if (Objects.isNull(user.getEmail()))
            throw new IllegalArgumentException("User email must be non null on creation");

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getById(UUID id){
        return userMapper.toDto(userRepository.findUserById(id).orElseThrow());
    }

    public Page<UserDto> getAll(Pageable pageable, String name, String surname){
        return userRepository.findAll(UserSpecification.userNameSpecification(name)
                .and(UserSpecification.userSurnameSpecification(surname)), pageable)
                .map(userMapper::toDto);
    }

    public Page<UserDto> getAll(Pageable pageable, UserFilter userFilter){
        return userRepository.findAll(UserSpecification.userNameSpecification(userFilter.getName())
                        .and(UserSpecification.userSurnameSpecification(userFilter.getSurname())), pageable)
                .map(userMapper::toDto);
    }

    @Transactional
    public UserDto updateById(UUID id, UserDto userDto){
        if (Objects.isNull(userDto.getName()))
            throw new IllegalArgumentException("User name must be non null on creation");
        if (Objects.isNull(userDto.getSurname()))
            throw new IllegalArgumentException("User surname must be non null on creation");
        if (Objects.isNull(userDto.getEmail()))
            throw new IllegalArgumentException("User email must be non null on creation");
        User user = userRepository.findUserById(id).orElseThrow();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setBirthDate(userDto.getBirthDate());

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(UserDto userDto){
        return updateById(userDto.getId(), userDto);
    }

    @Transactional
    public void activateById(UUID id){
        userRepository.activateUserById(id);
    }

    @Transactional
    public void deactivateById(UUID id){
        userRepository.deactivateUserById(id);
    }

    @Transactional
    public void deleteById(UUID id){
        userRepository.deleteById(id);
    }

}
