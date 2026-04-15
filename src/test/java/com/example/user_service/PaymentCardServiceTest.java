package com.example.user_service;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.entity.PaymentCard;
import com.example.user_service.entity.User;
import com.example.user_service.filter.SpecializationFilter;
import com.example.user_service.mapper.PaymentCardMapper;
import com.example.user_service.repository.PaymentCardRepository;
import com.example.user_service.service.PaymentCardService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private PaymentCardService paymentCardService;

    @Test
    void shouldReturnPaymentCardDtoWithIdAndCreatedAtAfterCreation(){
        // Arrange
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);

        PaymentCardDto paymentCardDto = new PaymentCardDto();
        paymentCardDto.setNumber("1234567890123456");
        paymentCardDto.setUserId(userId);
        paymentCardDto.setHolder("NAME SURNAME");
        paymentCardDto.setExpirationDate(LocalDate.of(2028, 12, 31));

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setNumber("1234567890123456");
        paymentCard.setUser(user);
        paymentCard.setHolder("NAME SURNAME");
        paymentCard.setExpirationDate(LocalDate.of(2028, 12, 31));

        var id = UUID.randomUUID();

        PaymentCard paymentCardDb = new PaymentCard();
        paymentCardDb.setId(id);
        paymentCardDb.setNumber("1234567890123456");
        paymentCardDb.setUser(user);
        paymentCardDb.setHolder("NAME SURNAME");
        paymentCardDb.setExpirationDate(LocalDate.of(2028, 12, 31));

        PaymentCardDto expectedPaymentCardDto = new PaymentCardDto();
        expectedPaymentCardDto.setId(id);
        expectedPaymentCardDto.setNumber("1234567890123456");
        expectedPaymentCardDto.setUserId(userId);
        expectedPaymentCardDto.setHolder("NAME SURNAME");
        expectedPaymentCardDto.setExpirationDate(LocalDate.of(2028, 12, 31));

        when(paymentCardMapper.toEntity(paymentCardDto)).thenReturn(paymentCard);
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCardDb);
        when(paymentCardMapper.toDto(paymentCardDb)).thenReturn(expectedPaymentCardDto);

        // Act
        var result = paymentCardService.create(paymentCardDto);

        // Assert
        verify(paymentCardMapper).toEntity(paymentCardDto);
        verify(paymentCardRepository).save(paymentCard);
        verify(paymentCardMapper).toDto(paymentCardDb);

        assertEquals(result, expectedPaymentCardDto);
    }

    @Test
    void shouldReturnPaymentCardDtoWhenIdExists(){
        // Arrange
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);

        PaymentCard paymentCardDb = new PaymentCard();
        paymentCardDb.setId(id);
        paymentCardDb.setNumber("1234567890123456");
        paymentCardDb.setUser(user);
        paymentCardDb.setHolder("NAME SURNAME");
        paymentCardDb.setExpirationDate(LocalDate.of(2028, 12, 31));

        PaymentCardDto expectedPaymentCardDto = new PaymentCardDto();
        expectedPaymentCardDto.setId(id);
        expectedPaymentCardDto.setNumber("1234567890123456");
        expectedPaymentCardDto.setUserId(userId);
        expectedPaymentCardDto.setHolder("NAME SURNAME");
        expectedPaymentCardDto.setExpirationDate(LocalDate.of(2028, 12, 31));

        when(paymentCardRepository.findPaymentCardById(id)).thenReturn(Optional.of(paymentCardDb));
        when(paymentCardMapper.toDto(paymentCardDb)).thenReturn(expectedPaymentCardDto);

        // Act
        var result = paymentCardService.getById(id);

        // Assert
        verify(paymentCardRepository).findPaymentCardById(id);
        verify(paymentCardMapper).toDto(paymentCardDb);

        assertEquals(result.getId(), expectedPaymentCardDto.getId());
        assertEquals(result, expectedPaymentCardDto);
    }

    @Test
    void shouldThrowPaymentCardNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentCardService.getById(id));

        // Assert
        assertEquals("Payment card with id: " + id + " is not found", exception.getMessage());
    }

    @Test
    void shouldReturnPageOfPaymentCardDto() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5, Sort.by("number"));
        SpecializationFilter filter = new SpecializationFilter("ja", "s", true);

        User user1 = new User();
        var user1Id = UUID.randomUUID();
        user1.setId(user1Id);
        user1.setName("Jack");
        user1.setSurname("Smith");

        User user2 = new User();
        var user2Id = UUID.randomUUID();
        user2.setId(user2Id);
        user2.setName("Jordan");
        user2.setSurname("Memos");

        PaymentCard p1 = new PaymentCard();
        var p1Id = UUID.randomUUID();
        p1.setId(p1Id);
        p1.setNumber("1234567890123456");
        p1.setUser(user1);
        p1.setHolder("NAME SURNAME");
        p1.setExpirationDate(LocalDate.of(2028, 12, 31));
        p1.setActive(true);

        PaymentCard p2 = new PaymentCard();
        var p2Id = UUID.randomUUID();
        p2.setId(p2Id);
        p2.setNumber("2345678901234561");
        p2.setUser(user1);
        p2.setHolder("NAMEE SURNAMEE");
        p2.setExpirationDate(LocalDate.of(2029, 8, 30));
        p2.setActive(true);

        PaymentCard p3 = new PaymentCard();
        var p3Id = UUID.randomUUID();
        p3.setId(p3Id);
        p3.setNumber("3456789012345612");
        p3.setUser(user1);
        p3.setHolder("NAM SURNAM");
        p3.setExpirationDate(LocalDate.of(2035, 1, 31));
        p3.setActive(true);

        PaymentCard p4 = new PaymentCard();
        var p4Id = UUID.randomUUID();
        p4.setId(p4Id);
        p4.setNumber("4567890123456123");
        p4.setUser(user2);
        p4.setHolder("NA SURNA");
        p4.setExpirationDate(LocalDate.of(2030, 10, 30));
        p4.setActive(true);

        PaymentCard p5 = new PaymentCard();
        var p5Id = UUID.randomUUID();
        p5.setId(p5Id);
        p5.setNumber("5678901234561234");
        p5.setUser(user2);
        p5.setHolder("NAMEME SURNAMEME");
        p5.setExpirationDate(LocalDate.of(2025, 4, 30));
        p5.setActive(false);

        Page<PaymentCard> page = new PageImpl<>(List.of(p1, p2, p3, p4, p5), pageable, 3);

        PaymentCardDto p1Dto = new PaymentCardDto();
        p1Dto.setId(p1Id);
        p1Dto.setNumber("1234567890123456");
        p1Dto.setUserId(user1.getId());
        p1Dto.setHolder("NAME SURNAME");
        p1Dto.setExpirationDate(LocalDate.of(2028, 12, 31));
        p1Dto.setActive(true);

        PaymentCardDto p2Dto = new PaymentCardDto();
        p2Dto.setId(p2Id);
        p2Dto.setNumber("2345678901234561");
        p2Dto.setUserId(user1.getId());
        p2Dto.setHolder("NAMEE SURNAMEE");
        p2Dto.setExpirationDate(LocalDate.of(2028, 12, 31));
        p2Dto.setActive(true);

        PaymentCardDto p3Dto = new PaymentCardDto();
        p3Dto.setId(p3Id);
        p3Dto.setNumber("3456789012345612");
        p3Dto.setUserId(user1.getId());
        p3Dto.setHolder("NAM SURNAM");
        p3Dto.setExpirationDate(LocalDate.of(2035, 1, 31));
        p3Dto.setActive(true);

        PaymentCardDto p4Dto = new PaymentCardDto();
        p4Dto.setId(p4Id);
        p4Dto.setNumber("4567890123456123");
        p4Dto.setUserId(user2.getId());
        p4Dto.setHolder("NA SURNA");
        p4Dto.setExpirationDate(LocalDate.of(2030, 10, 30));
        p4Dto.setActive(true);

        PaymentCardDto p5Dto = new PaymentCardDto();
        p5Dto.setId(p5Id);
        p5Dto.setNumber("5678901234561234");
        p5Dto.setUserId(user2.getId());
        p5Dto.setHolder("NAMEME SURNAMEME");
        p5Dto.setExpirationDate(LocalDate.of(2025, 3, 31));
        p5Dto.setActive(false);

        Page<PaymentCardDto> expectedPage = new PageImpl<>(List.of(p1Dto, p2Dto, p3Dto, p4Dto, p5Dto), pageable, 3);

        when(paymentCardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(paymentCardMapper.toDto(p1)).thenReturn(p1Dto);
        when(paymentCardMapper.toDto(p2)).thenReturn(p2Dto);
        when(paymentCardMapper.toDto(p3)).thenReturn(p3Dto);
        when(paymentCardMapper.toDto(p4)).thenReturn(p4Dto);
        when(paymentCardMapper.toDto(p5)).thenReturn(p5Dto);

        // Act
        var result = paymentCardService.getAll(pageable, filter);

        // Assert
        verify(paymentCardRepository).findAll(any(Specification.class), eq(pageable));
        verify(paymentCardMapper).toDto(p1);
        verify(paymentCardMapper).toDto(p2);
        verify(paymentCardMapper).toDto(p3);
        verify(paymentCardMapper).toDto(p4);
        verify(paymentCardMapper).toDto(p5);

        assertEquals(expectedPage.getContent(), result.getContent());

    }

    @Test
    void shouldReturnUserPaymentCardsIfUserIdExists(){
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());

        PaymentCard p1 = new PaymentCard();
        var p1Id = UUID.randomUUID();
        p1.setId(p1Id);
        p1.setNumber("1234567890123456");
        p1.setUser(user);
        p1.setHolder("NAME SURNAME");
        p1.setExpirationDate(LocalDate.of(2028, 12, 31));
        p1.setActive(true);

        PaymentCard p2 = new PaymentCard();
        var p2Id = UUID.randomUUID();
        p2.setId(p2Id);
        p2.setNumber("2345678901234561");
        p2.setUser(user);
        p2.setHolder("NAMEE SURNAMEE");
        p2.setExpirationDate(LocalDate.of(2029, 7, 31));
        p2.setActive(true);

        PaymentCard p3 = new PaymentCard();
        var p3Id = UUID.randomUUID();
        p3.setId(p3Id);
        p3.setNumber("3456789012345612");
        p3.setUser(user);
        p3.setHolder("NAM SURNAM");
        p3.setExpirationDate(LocalDate.of(2035, 1, 31));
        p3.setActive(true);

        PaymentCard p4 = new PaymentCard();
        var p4Id = UUID.randomUUID();
        p4.setId(p4Id);
        p4.setNumber("4567890123456123");
        p4.setUser(user);
        p4.setHolder("NA SURNA");
        p4.setExpirationDate(LocalDate.of(2024, 10, 31));
        p4.setActive(false);

        List<PaymentCard> list = List.of(p1, p2, p3, p4);

        PaymentCardDto p1Dto = new PaymentCardDto();
        p1Dto.setId(p1Id);
        p1Dto.setNumber("1234567890123456");
        p1Dto.setUserId(user.getId());
        p1Dto.setHolder("NAME SURNAME");
        p1Dto.setExpirationDate(LocalDate.of(2028, 12, 31));
        p1Dto.setActive(true);

        PaymentCardDto p2Dto = new PaymentCardDto();
        p2Dto.setId(p2Id);
        p2Dto.setNumber("2345678901234561");
        p2Dto.setUserId(user.getId());
        p2Dto.setHolder("NAMEE SURNAMEE");
        p2Dto.setExpirationDate(LocalDate.of(2029, 7, 31));
        p2Dto.setActive(true);

        PaymentCardDto p3Dto = new PaymentCardDto();
        p3Dto.setId(p3Id);
        p3Dto.setNumber("3456789012345612");
        p3Dto.setUserId(user.getId());
        p3Dto.setHolder("NAM SURNAM");
        p3Dto.setExpirationDate(LocalDate.of(2035, 1, 31));
        p3Dto.setActive(true);

        PaymentCardDto p4Dto = new PaymentCardDto();
        p4Dto.setId(p4Id);
        p4Dto.setNumber("4567890123456123");
        p4Dto.setUserId(user.getId());
        p4Dto.setHolder("NA SURNA");
        p4Dto.setExpirationDate(LocalDate.of(2024, 10, 31));
        p4Dto.setActive(false);

        List<PaymentCardDto> listDto = List.of(p1Dto, p2Dto, p3Dto, p4Dto);

        when(paymentCardRepository.findAllByUserId(user.getId())).thenReturn(list);
        when(paymentCardMapper.toDto(p1)).thenReturn(p1Dto);
        when(paymentCardMapper.toDto(p2)).thenReturn(p2Dto);
        when(paymentCardMapper.toDto(p3)).thenReturn(p3Dto);
        when(paymentCardMapper.toDto(p4)).thenReturn(p4Dto);

        // Act
        var result = paymentCardService.getAllByUserId(user.getId());

        // Assert
        verify(paymentCardRepository).findAllByUserId(user.getId());
        verify(paymentCardMapper).toDto(p1);
        verify(paymentCardMapper).toDto(p2);
        verify(paymentCardMapper).toDto(p3);
        verify(paymentCardMapper).toDto(p4);

        assertEquals(result, listDto);
    }

    @Test
    void shouldReturnRefreshedPaymentCardAfterUpdating() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(4);
        LocalDateTime updatedAt = LocalDateTime.now();

        PaymentCardDto paymentCardDto = new PaymentCardDto();
        paymentCardDto.setId(id);
        paymentCardDto.setNumber("0987654321654321");
        paymentCardDto.setUserId(userId);
        paymentCardDto.setHolder("EMANRUS EMAN");
        paymentCardDto.setExpirationDate(LocalDate.of(2031, 9, 30));
        paymentCardDto.setCreatedAt(createdAt);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(id);
        paymentCard.setNumber("1234567890123456");
        paymentCard.setUser(user);
        paymentCard.setHolder("NAME SURNAME");
        paymentCard.setExpirationDate(LocalDate.of(2028, 12, 31));
        paymentCard.setCreatedAt(createdAt);

        PaymentCard paymentCardDb = new PaymentCard();
        paymentCardDb.setId(id);
        paymentCardDb.setNumber("0987654321654321");
        paymentCardDb.setUser(user);
        paymentCardDb.setHolder("EMANRUS EMAN");
        paymentCardDb.setExpirationDate(LocalDate.of(2031, 9, 30));
        paymentCardDb.setCreatedAt(createdAt);
        paymentCardDb.setUpdatedAt(updatedAt);

        PaymentCardDto expectedPaymentCardDto = new PaymentCardDto();
        expectedPaymentCardDto.setId(id);
        expectedPaymentCardDto.setNumber("0987654321654321");
        expectedPaymentCardDto.setUserId(userId);
        expectedPaymentCardDto.setHolder("EMANRUS EMAN");
        expectedPaymentCardDto.setExpirationDate(LocalDate.of(2031, 9, 30));
        expectedPaymentCardDto.setCreatedAt(createdAt);
        expectedPaymentCardDto.setUpdatedAt(updatedAt);

        when(paymentCardRepository.findPaymentCardById(id)).thenReturn(Optional.of(paymentCard));
        when(paymentCardRepository.saveAndFlush(paymentCard)).thenReturn(paymentCardDb);
        when(paymentCardMapper.toDto(paymentCardDb)).thenReturn(expectedPaymentCardDto);

        // Act
        var result = paymentCardService.update(paymentCardDto);

        // Assert
        verify(paymentCardRepository).saveAndFlush(paymentCard);
        verify(paymentCardRepository).findPaymentCardById(id);
        verify(paymentCardMapper).toDto(paymentCardDb);

        assertEquals(result, expectedPaymentCardDto);
    }

    @Test
    void shouldReturnActivatedPaymentDtoAfterActivation() {
        // Arrange
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(4);
        LocalDateTime updatedAt = LocalDateTime.now();

        PaymentCard paymentCardDb = new PaymentCard();
        paymentCardDb.setId(id);
        paymentCardDb.setNumber("0987654321654321");
        paymentCardDb.setUser(user);
        paymentCardDb.setHolder("EMANRUS EMAN");
        paymentCardDb.setExpirationDate(LocalDate.of(2031, 9, 30));
        paymentCardDb.setActive(true);
        paymentCardDb.setCreatedAt(createdAt);
        paymentCardDb.setUpdatedAt(updatedAt);

        PaymentCardDto expectedPaymentCardDto = new PaymentCardDto();
        expectedPaymentCardDto.setId(id);
        expectedPaymentCardDto.setNumber("0987654321654321");
        expectedPaymentCardDto.setUserId(userId);
        expectedPaymentCardDto.setHolder("EMANRUS EMAN");
        expectedPaymentCardDto.setExpirationDate(LocalDate.of(2031, 9, 30));
        expectedPaymentCardDto.setActive(true);
        expectedPaymentCardDto.setCreatedAt(createdAt);
        expectedPaymentCardDto.setUpdatedAt(updatedAt);

        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(paymentCardDb));
        when(paymentCardRepository.save(paymentCardDb)).thenReturn(paymentCardDb);
        when(paymentCardMapper.toDto(paymentCardDb)).thenReturn(expectedPaymentCardDto);

        // Act
        var result = paymentCardService.activateById(id);

        // Assert
        verify(paymentCardRepository).findById(id);
        verify(paymentCardRepository).save(paymentCardDb);
        verify(paymentCardMapper).toDto(paymentCardDb);

        assertEquals(result, expectedPaymentCardDto);
    }

    @Test
    void shouldReturnDeactivatedUserDtoAfterActivation() {
        // Arrange
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(4);
        LocalDateTime updatedAt = LocalDateTime.now();

        PaymentCard paymentCardDb = new PaymentCard();
        paymentCardDb.setId(id);
        paymentCardDb.setNumber("0987654321654321");
        paymentCardDb.setUser(user);
        paymentCardDb.setHolder("EMANRUS EMAN");
        paymentCardDb.setExpirationDate(LocalDate.of(2031, 9, 30));
        paymentCardDb.setActive(false);
        paymentCardDb.setCreatedAt(createdAt);
        paymentCardDb.setUpdatedAt(updatedAt);

        PaymentCardDto expectedPaymentCardDto = new PaymentCardDto();
        expectedPaymentCardDto.setId(id);
        expectedPaymentCardDto.setNumber("0987654321654321");
        expectedPaymentCardDto.setUserId(userId);
        expectedPaymentCardDto.setHolder("EMANRUS EMAN");
        expectedPaymentCardDto.setExpirationDate(LocalDate.of(2031, 9, 30));
        expectedPaymentCardDto.setActive(false);
        expectedPaymentCardDto.setCreatedAt(createdAt);
        expectedPaymentCardDto.setUpdatedAt(updatedAt);

        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(paymentCardDb));
        when(paymentCardRepository.save(paymentCardDb)).thenReturn(paymentCardDb);
        when(paymentCardMapper.toDto(paymentCardDb)).thenReturn(expectedPaymentCardDto);

        // Act
        var result = paymentCardService.deactivateById(id);

        // Assert
        verify(paymentCardRepository).findById(id);
        verify(paymentCardRepository).save(paymentCardDb);
        verify(paymentCardMapper).toDto(paymentCardDb);

        assertEquals(result, expectedPaymentCardDto);
    }

    @Test
    void shouldDeleteUserById() {
        // given
        UUID id = UUID.randomUUID();

        // when
        paymentCardService.deleteById(id);

        // then
        verify(paymentCardRepository).deleteById(id);
        verifyNoMoreInteractions(paymentCardRepository);
    }

}
