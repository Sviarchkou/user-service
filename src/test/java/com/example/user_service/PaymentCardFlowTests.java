package com.example.user_service;

import com.example.user_service.dto.PaymentCardDto;
import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.PaymentCard;
import com.example.user_service.entity.User;
import com.example.user_service.repository.PaymentCardRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.PaymentCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentCardFlowTests {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Container
    @ServiceConnection
    public static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PaymentCardRepository paymentCardRepository;
    @Autowired
    private PaymentCardService paymentCardService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createPaymentCard(){
        var user = new User();
        user.setName("Michael");
        user.setSurname("Jackson");
        user.setEmail("heheejackson@gmail.com");
        user = userRepository.save(user);


        PaymentCardDto paymentCardDto = new PaymentCardDto();
        paymentCardDto.setNumber("1234567890123456");
        paymentCardDto.setUserId(user.getId());
        paymentCardDto.setHolder("NAME SURNAME");
        paymentCardDto.setExpirationDate(LocalDate.of(2028, 12, 31));

        ResponseEntity<PaymentCardDto> response = restTemplate.postForEntity("/api/v1/cards", paymentCardDto, PaymentCardDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(paymentCardDto.getNumber(), response.getBody().getNumber());
        assertEquals(paymentCardDto.getHolder(), response.getBody().getHolder());
        assertEquals(paymentCardDto.getExpirationDate(), response.getBody().getExpirationDate());
        assertEquals(paymentCardDto.getUserId(), response.getBody().getUserId());
        assertNotNull(response.getBody().getCreatedAt());
    }

    @Test
    void getPaymentCardByIdWhenExists(){
        User user = new User();
        user.setName("Donald");
        user.setSurname("Trump");
        user.setEmail("trumpdnld@gmail.com");

        user = userRepository.save(user);

        PaymentCardDto paymentCardDto = new PaymentCardDto();
        paymentCardDto.setNumber("2345678901234561");
        paymentCardDto.setUserId(user.getId());
        paymentCardDto.setHolder("DONALD TRUMP");
        paymentCardDto.setExpirationDate(LocalDate.of(2028, 12, 31));

        paymentCardDto = paymentCardService.create(paymentCardDto);

        var response = restTemplate.getForEntity("/api/v1/cards/" + paymentCardDto.getId(), PaymentCardDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(paymentCardDto.getNumber(), response.getBody().getNumber());
        assertEquals(paymentCardDto.getHolder(), response.getBody().getHolder());
        assertEquals(paymentCardDto.getExpirationDate(), response.getBody().getExpirationDate());
        assertEquals(paymentCardDto.getUserId(), response.getBody().getUserId());
        assertNotNull(response.getBody().getCreatedAt());
    }

    @Test
    void getUserByIdWhenDoesNotExists(){
        var response = restTemplate.getForEntity("/api/v1/cards/" + UUID.randomUUID(), UserDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllPaymentCards(){
        User user1 = new User();
        user1.setName("Jack");
        user1.setSurname("Smith");
        user1.setEmail("jacksmith@gmail.com");

        User user2 = new User();
        user2.setName("Jordan");
        user2.setSurname("Smith");
        user2.setEmail("jordansmith@gmail.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        PaymentCard p1 = new PaymentCard();
        p1.setNumber("6789012345612345");
        p1.setUser(user1);
        p1.setHolder("JACK SMITH");
        p1.setExpirationDate(LocalDate.of(2028, 12, 31));
        p1.setActive(true);

        PaymentCard p2 = new PaymentCard();
        p2.setNumber("7890123456123456");
        p2.setUser(user1);
        p2.setHolder("JACK SMITH");
        p2.setExpirationDate(LocalDate.of(2029, 8, 30));
        p2.setActive(true);

        PaymentCard p3 = new PaymentCard();
        p3.setNumber("3456789012345612");
        p3.setUser(user1);
        p3.setHolder("JACK SMITH");
        p3.setExpirationDate(LocalDate.of(2035, 1, 31));
        p3.setActive(true);

        PaymentCard p4 = new PaymentCard();
        p4.setNumber("4567890123456123");
        p4.setUser(user2);
        p4.setHolder("JORDAN SMITH");
        p4.setExpirationDate(LocalDate.of(2030, 10, 31));
        p4.setActive(true);

        PaymentCard p5 = new PaymentCard();
        p5.setNumber("5678901234561234");
        p5.setUser(user2);
        p5.setHolder("JORDAN SMITH");
        p5.setExpirationDate(LocalDate.of(2025, 4, 30));
        p5.setActive(false);

        paymentCardRepository.save(p1);
        paymentCardRepository.save(p2);
        paymentCardRepository.save(p3);
        paymentCardRepository.save(p4);
        paymentCardRepository.save(p5);

        //// Type definition error: [simple type, class org.springframework.data.domain.Sort]
        ResponseEntity<PageResponse<PaymentCardDto>> response = restTemplate.exchange(
                "/api/v1/cards?name=j&surname=smith&page=0&size=3&sort=number,asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals("3456789012345612", response.getBody().getContent().get(0).getNumber());
        assertEquals("4567890123456123", response.getBody().getContent().get(1).getNumber());
        assertEquals("5678901234561234", response.getBody().getContent().get(2).getNumber());
    }

    @Test
    void getAllUserCards(){
        User user = new User();
        user.setName("Jason");
        user.setSurname("Richardson");
        user.setEmail("jasonrichardson@gmail.com");

        user = userRepository.save(user);

        PaymentCardDto p1Dto = new PaymentCardDto();
        p1Dto.setNumber("9999888877776666");
        p1Dto.setUserId(user.getId());
        p1Dto.setHolder("JASON RICHARDSON");
        p1Dto.setExpirationDate(LocalDate.of(2028, 12, 31));
        p1Dto.setActive(true);

        PaymentCardDto p2Dto = new PaymentCardDto();
        p2Dto.setNumber("9999777755553333");
        p2Dto.setUserId(user.getId());
        p2Dto.setHolder("JASON RICHARDSON");
        p2Dto.setExpirationDate(LocalDate.of(2029, 9, 30));
        p2Dto.setActive(true);

        PaymentCardDto p3Dto = new PaymentCardDto();
        p3Dto.setNumber("8888666644442222");
        p3Dto.setUserId(user.getId());
        p3Dto.setHolder("JASON RICHARDSON");
        p3Dto.setExpirationDate(LocalDate.of(2035, 1, 31));
        p3Dto.setActive(true);

        PaymentCardDto p4Dto = new PaymentCardDto();
        p4Dto.setNumber("9911882277336644");
        p4Dto.setUserId(user.getId());
        p4Dto.setHolder("JASON RICHARDSON");
        p4Dto.setExpirationDate(LocalDate.of(2024, 4, 30));
        p4Dto.setActive(false);

        p1Dto = paymentCardService.create(p1Dto);
        p2Dto = paymentCardService.create(p2Dto);
        p3Dto = paymentCardService.create(p3Dto);
        p4Dto = paymentCardService.create(p4Dto);

        List<PaymentCardDto> listDto = List.of(p1Dto, p2Dto, p3Dto, p4Dto);

        ResponseEntity<List<PaymentCardDto>> response = restTemplate.exchange(
                "/api/v1/users/" + user.getId() + "/cards",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        response.getBody().forEach(p -> {
            p.setCreatedAt(p.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
            p.setUpdatedAt(p.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        });
        listDto.forEach(p -> {
            p.setCreatedAt(p.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
            p.setUpdatedAt(p.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        });

        assertEquals(listDto, response.getBody());
    }

    @Test
    void updateUserById(){
        User user = new User();
        user.setName("Leonel");
        user.setSurname("Messi");
        user.setEmail("messi@gmail.com");

        user = userRepository.save(user);

        PaymentCardDto paymentCardDto = new PaymentCardDto();
        paymentCardDto.setNumber("1098765432165432");
        paymentCardDto.setUserId(user.getId());
        paymentCardDto.setHolder("LEONEL MESSI");
        paymentCardDto.setExpirationDate(LocalDate.of(2032, 12, 31));

        paymentCardDto = paymentCardService.create(paymentCardDto);

        paymentCardDto.setHolder("MESSI LEONEL");
        paymentCardDto.setNumber("9999999999999999");

        ResponseEntity<PaymentCardDto> response = restTemplate.exchange(
                "/api/v1/cards/" + paymentCardDto.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(paymentCardDto),
                PaymentCardDto.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(paymentCardDto.getNumber(), response.getBody().getNumber());
        assertEquals(paymentCardDto.getHolder(), response.getBody().getHolder());
        assertEquals(paymentCardDto.getExpirationDate(), response.getBody().getExpirationDate());
        assertEquals(paymentCardDto.getUserId(), response.getBody().getUserId());
        assertNotNull(response.getBody().getCreatedAt());
        assertNotNull(response.getBody().getUpdatedAt());
    }

    @Test
    void changePaymentCardActivityById(){
        User user = new User();
        user.setName("Tosin");
        user.setSurname("Abasi");
        user.setEmail("abasiconcept@gmail.com");

        user = userRepository.save(user);

        PaymentCardDto paymentCardDto = new PaymentCardDto();
        paymentCardDto.setNumber("0987654321654321");
        paymentCardDto.setUserId(user.getId());
        paymentCardDto.setHolder("EMANRUS EMAN");
        paymentCardDto.setExpirationDate(LocalDate.of(2031, 9, 30));
        paymentCardDto.setActive(false);

        paymentCardDto = paymentCardService.create(paymentCardDto);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/cards/" + paymentCardDto.getId() + "/activate",
                HttpMethod.PUT,
                null,
                Void.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        var paymentCard = paymentCardRepository.findPaymentCardById(paymentCardDto.getId()).orElseThrow();
        assertTrue(paymentCard.isActive());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deletePaymentCardById(){
        User user = new User();
        user.setName("Nikolay");
        user.setSurname("Baskov");
        user.setEmail("zolotyachasha@gmail.com");

        user = userRepository.save(user);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setNumber("0000111122223333");
        paymentCard.setUser(user);
        paymentCard.setHolder("NIKOLAY BASKOV");
        paymentCard.setExpirationDate(LocalDate.of(2031, 9, 30));

        paymentCard = paymentCardRepository.save(paymentCard);
        // when
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/cards/" + paymentCard.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(paymentCardRepository.findPaymentCardById(paymentCard.getId()).isEmpty());
    }

    private static class PageResponse<T> {
        private List<T> content;
        private int number;
        private int size;
        private int totalPages;
        private long totalElements;
        private boolean first;
        private boolean last;

        public void setContent(List<T> content) {
            this.content = content;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public List<T> getContent() {
            return content;
        }

        public int getNumber() {
            return number;
        }

        public int getSize() {
            return size;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public boolean isFirst() {
            return first;
        }

        public boolean isLast() {
            return last;
        }

    }

}
