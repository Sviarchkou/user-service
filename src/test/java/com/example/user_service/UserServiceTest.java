package com.example.user_service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.User;
import com.example.user_service.filter.SpecializationFilter;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.request.UserIdListRequest;
import com.example.user_service.service.UserService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator validator;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserDtoWithIdAndCreatedAtAfterCreation(){
        // Arrange
        UserDto mockUserDto = new UserDto();
        mockUserDto.setName("Jack");
        mockUserDto.setSurname("Smith");
        mockUserDto.setEmail("jachsparrrrrow@gmail.com");

        User user = new User();
        user.setName("Jack");
        user.setSurname("Smith");
        user.setEmail("jachsparrrrrow@gmail.com");

        var id = UUID.randomUUID();
        User dbUser = new User();
        dbUser.setId(id);
        dbUser.setName("Jack");
        dbUser.setSurname("Smith");
        dbUser.setEmail("jachsparrrrrow@gmail.com");
        dbUser.setCreatedAt(LocalDateTime.now());

        UserDto expectedDto = new UserDto();
        expectedDto.setId(id);
        expectedDto.setName("Jack");
        expectedDto.setSurname("Smith");
        expectedDto.setEmail("jachsparrrrrow@gmail.com");
        expectedDto.setCreatedAt(LocalDateTime.now());

        when(userMapper.toEntity(mockUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(dbUser);
        when(userMapper.toDto(dbUser)).thenReturn(expectedDto);

        // Act
        UserDto result = userService.create(mockUserDto);

        // Assert
        verify(userMapper).toEntity(mockUserDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(dbUser);

        assertEquals(result, expectedDto);
    }

    @Test
    void shouldReturnUserDtoWhenIdExists() {
        // Arrange
        var id = UUID.randomUUID();

        User dbUser = new User();
        dbUser.setId(id);
        dbUser.setName("Jack");
        dbUser.setSurname("Smith");
        dbUser.setEmail("jachsparrrrrow@gmail.com");
        dbUser.setCreatedAt(LocalDateTime.now());

        UserDto expectedDto = new UserDto();
        expectedDto.setId(id);
        expectedDto.setName("Jack");
        expectedDto.setSurname("Smith");
        expectedDto.setEmail("jachsparrrrrow@gmail.com");
        expectedDto.setCreatedAt(LocalDateTime.now());

        when(userRepository.findUserById(id)).thenReturn(Optional.of(dbUser));
        when(userMapper.toDto(dbUser)).thenReturn(expectedDto);

        // Act
        UserDto result = userService.getById(id);

        // Assert
        verify(userRepository).findUserById(id);
        verify(userMapper).toDto(dbUser);

        assertEquals(result.getId(), expectedDto.getId());
        assertEquals(result, expectedDto);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getById(id));

        // Assert
        assertEquals("User with id: " + id + " is not found", exception.getMessage());
    }

    @Test
    void shouldReturnPageOfUserDto() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        SpecializationFilter filter = new SpecializationFilter("ja", "s", true);

        User dbUser1 = new User();
        var user1Id = UUID.randomUUID();
        dbUser1.setId(user1Id);
        dbUser1.setName("Jack");
        dbUser1.setSurname("Smith");
        dbUser1.setEmail("jachsparrrrrow@gmail.com");
        dbUser1.setActive(true);
        dbUser1.setCreatedAt(LocalDateTime.now());

        User dbUser2 = new User();
        var user2Id = UUID.randomUUID();
        dbUser2.setId(user2Id);
        dbUser2.setName("Jordan");
        dbUser2.setSurname("Memos");
        dbUser2.setEmail("jordanniiiiii@gmail.com");
        dbUser2.setActive(true);
        dbUser2.setCreatedAt(LocalDateTime.now());

        User dbUser3 = new User();
        var user3Id = UUID.randomUUID();
        dbUser3.setId(user3Id);
        dbUser3.setName("Rojo");
        dbUser3.setSurname("Hamsford");
        dbUser3.setEmail("probert@gmail.com");
        dbUser3.setActive(true);
        dbUser3.setCreatedAt(LocalDateTime.now());

        Page<User> page = new PageImpl<>(List.of(dbUser1, dbUser2, dbUser3), pageable, 3);

        UserDto expectedDto1 = new UserDto();
        expectedDto1.setId(user1Id);
        expectedDto1.setName("Jack");
        expectedDto1.setSurname("Smith");
        expectedDto1.setEmail("jachsparrrrrow@gmail.com");
        expectedDto1.setActive(true);
        expectedDto1.setCreatedAt(LocalDateTime.now());

        UserDto expectedDto2 = new UserDto();
        expectedDto2.setId(user2Id);
        expectedDto2.setName("Jordan");
        expectedDto2.setSurname("Memos");
        expectedDto2.setEmail("jordanniiiiii@gmail.com");
        expectedDto2.setActive(true);
        expectedDto2.setCreatedAt(LocalDateTime.now());

        UserDto expectedDto3 = new UserDto();
        expectedDto3.setId(user3Id);
        expectedDto3.setName("Rojo");
        expectedDto3.setSurname("Hamsford");
        expectedDto3.setEmail("probert@gmail.com");
        expectedDto3.setActive(true);
        expectedDto3.setCreatedAt(LocalDateTime.now());

        Page<UserDto> expectedPage = new PageImpl<>(List.of(expectedDto1, expectedDto2, expectedDto3), pageable, 3);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(userMapper.toDto(dbUser1)).thenReturn(expectedDto1);
        when(userMapper.toDto(dbUser2)).thenReturn(expectedDto2);
        when(userMapper.toDto(dbUser3)).thenReturn(expectedDto3);

        // Act
        var result = userService.getAll(pageable, filter);

        // Assert
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toDto(dbUser1);
        verify(userMapper).toDto(dbUser2);
        verify(userMapper).toDto(dbUser3);

        assertEquals(expectedPage.getContent(), result.getContent());

    }

    @Test
    void shouldReturnListOfUserDto(){
        User dbUser1 = new User();
        var user1Id = UUID.randomUUID();
        dbUser1.setId(user1Id);
        dbUser1.setName("Jack");

        User dbUser2 = new User();
        var user2Id = UUID.randomUUID();
        dbUser2.setId(user2Id);
        dbUser2.setName("Jordan");

        User dbUser3 = new User();
        var user3Id = UUID.randomUUID();
        dbUser3.setId(user3Id);
        dbUser3.setName("Rojo");

        UserDto expectedDto1 = new UserDto();
        expectedDto1.setId(user1Id);
        expectedDto1.setName("Jack");

        UserDto expectedDto2 = new UserDto();
        expectedDto2.setId(user2Id);
        expectedDto2.setName("Jordan");

        UserDto expectedDto3 = new UserDto();
        expectedDto3.setId(user3Id);
        expectedDto3.setName("Rojo");

        UserIdListRequest request = new UserIdListRequest(List.of(user1Id, user2Id, user3Id));
        List<User> dbUsers = List.of(dbUser1, dbUser2, dbUser3);
        List<UserDto> userDtos = List.of(expectedDto1, expectedDto2, expectedDto3);

        when(userRepository.findAllByIdIn(request.userIdList())).thenReturn(dbUsers);
        when(userMapper.toDto(dbUser1)).thenReturn(expectedDto1);
        when(userMapper.toDto(dbUser2)).thenReturn(expectedDto2);
        when(userMapper.toDto(dbUser3)).thenReturn(expectedDto3);

        // Act
        var result = userService.getAllByUserIdList(request);

        // Assert
        verify(userRepository).findAllByIdIn(request.userIdList());
        verify(userMapper).toDto(dbUser1);
        verify(userMapper).toDto(dbUser2);
        verify(userMapper).toDto(dbUser3);

        assertEquals(result, userDtos);
    }

    @Test
    void shouldReturnRefreshedUserAfterUpdating() {
        // Arrange
        var id = UUID.randomUUID();
        var createdAt = LocalDateTime.now().minusMinutes(5);

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName("Jack");
        userDto.setSurname("Jackson");
        userDto.setEmail("jarrow@gmail.com");
        userDto.setActive(true);
        userDto.setCreatedAt(createdAt);

        User userDb = new User();
        userDb.setId(id);
        userDb.setName("Jack");
        userDb.setSurname("Smith");
        userDb.setEmail("jachsparrrrrow@gmail.com");
        userDb.setActive(true);
        userDb.setCreatedAt(createdAt);

        User updatedUserDb = new User();
        updatedUserDb.setId(id);
        updatedUserDb.setName("Jack");
        updatedUserDb.setSurname("Jackson");
        updatedUserDb.setEmail("jarrow@gmail.com");
        updatedUserDb.setActive(true);
        updatedUserDb.setCreatedAt(createdAt);
        updatedUserDb.setUpdatedAt(LocalDateTime.now());

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(id);
        expectedUserDto.setName("Jack");
        expectedUserDto.setSurname("Smith");
        expectedUserDto.setEmail("jachsparrrrrow@gmail.com");
        expectedUserDto.setActive(true);
        expectedUserDto.setCreatedAt(createdAt);

        when(userRepository.findUserById(id)).thenReturn(Optional.of(userDb));
        when(userRepository.saveAndFlush(userDb)).thenReturn(updatedUserDb);
        when(userMapper.toDto(updatedUserDb)).thenReturn(expectedUserDto);

        // Act
        var result = userService.update(userDto);

        // Assert
        verify(userRepository).findUserById(id);
        verify(userRepository).saveAndFlush(userDb);
        verify(userMapper).toDto(updatedUserDb);

        assertEquals(result, expectedUserDto);
    }

    @Test
    void shouldReturnActivatedUserDtoAfterActivation() {
        // Arrange
        var id = UUID.randomUUID();
        User dbUser = new User();
        dbUser.setId(id);
        dbUser.setName("Jack");
        dbUser.setSurname("Smith");
        dbUser.setEmail("jachsparrrrrow@gmail.com");
        dbUser.setActive(true);
        dbUser.setCreatedAt(LocalDateTime.now());

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(id);
        expectedUserDto.setName("Jack");
        expectedUserDto.setSurname("Smith");
        expectedUserDto.setEmail("jachsparrrrrow@gmail.com");
        expectedUserDto.setActive(true);
        expectedUserDto.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(Optional.of(dbUser));
        when(userRepository.save(dbUser)).thenReturn(dbUser);
        when(userMapper.toDto(dbUser)).thenReturn(expectedUserDto);

        // Act
        var result = userService.activateById(id);

        // Assert
        verify(userRepository).findById(id);
        verify(userRepository).save(dbUser);
        verify(userMapper).toDto(dbUser);

        assertEquals(result, expectedUserDto);
    }

    @Test
    void shouldReturnDeactivatedUserDtoAfterActivation() {
        // Arrange
        var id = UUID.randomUUID();
        User dbUser = new User();
        dbUser.setId(id);
        dbUser.setName("Jack");
        dbUser.setSurname("Smith");
        dbUser.setEmail("jachsparrrrrow@gmail.com");
        dbUser.setActive(false);
        dbUser.setCreatedAt(LocalDateTime.now());

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(id);
        expectedUserDto.setName("Jack");
        expectedUserDto.setSurname("Smith");
        expectedUserDto.setEmail("jachsparrrrrow@gmail.com");
        expectedUserDto.setActive(false);
        expectedUserDto.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(Optional.of(dbUser));
        when(userRepository.save(dbUser)).thenReturn(dbUser);
        when(userMapper.toDto(dbUser)).thenReturn(expectedUserDto);

        // Act
        var result = userService.deactivateById(id);

        // Assert
        verify(userRepository).findById(id);
        verify(userRepository).save(dbUser);
        verify(userMapper).toDto(dbUser);

        assertEquals(result, expectedUserDto);
    }

    @Test
    void shouldDeleteUserById() {
        // given
        UUID id = UUID.randomUUID();

        // when
        userService.deleteById(id);

        // then
        verify(userRepository).deleteById(id);
        verifyNoMoreInteractions(userRepository);
    }


}
