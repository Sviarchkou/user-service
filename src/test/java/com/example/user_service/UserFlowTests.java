package com.example.user_service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.repository.PaymentCardRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserFlowTests {

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
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentCardRepository paymentCardRepository;

	@Test
	void createUser(){
		UserDto userDto = new UserDto();
		userDto.setName("Michael");
		userDto.setSurname("Jackson");
		userDto.setEmail("heheejackson@gmail.com");
		userDto.setActive(true);

		ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/v1/users", userDto, UserDto.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());
		assertEquals(userDto.getName(), response.getBody().getName());
		assertEquals(userDto.getSurname(), response.getBody().getSurname());
		assertEquals(userDto.getEmail(), response.getBody().getEmail());
		assertNotNull(response.getBody().getCreatedAt());
	}

	@Test
	void getUserByIdWhenExists(){
		UserDto userDto = new UserDto();
		userDto.setName("Donald");
		userDto.setSurname("Trump");
		userDto.setEmail("trumpdnld@gmail.com");

		userDto = userService.create(userDto);

		var response = restTemplate.getForEntity("/api/v1/users/" + userDto.getId(), UserDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());
		assertEquals(userDto.getName(), response.getBody().getName());
		assertEquals(userDto.getSurname(), response.getBody().getSurname());
		assertEquals(userDto.getEmail(), response.getBody().getEmail());
	}

	@Test
	void getUserByIdWhenDoesNotExists(){
		var response = restTemplate.getForEntity("/api/v1/users/" + UUID.randomUUID(), UserDto.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void getAllUsers(){
		UserDto userDto1 = new UserDto();
		userDto1.setName("User1ja");
		userDto1.setSurname("surname1");
		userDto1.setEmail("email1@gmail.com");

		UserDto userDto2 = new UserDto();
		userDto2.setName("UsJAer2");
		userDto2.setSurname("Surname2SS");
		userDto2.setEmail("email2@gmail.com");

		UserDto userDto3 = new UserDto();
		userDto3.setName("User3ja");
		userDto3.setSurname("urname3");
		userDto3.setEmail("email3@gmail.com");

		UserDto userDto4 = new UserDto();
		userDto4.setName("User4");
		userDto4.setSurname("Surname4");
		userDto4.setEmail("email4@gmail.com");

		UserDto userDto5 = new UserDto();
		userDto5.setName("jAUser5");
		userDto5.setSurname("Surname5");
		userDto5.setEmail("email5@gmail.com");

		UserDto userDto6 = new UserDto();
		userDto6.setName("User6jAAAAAA");
		userDto6.setSurname("Surname6");
		userDto6.setEmail("email6@gmail.com");

		userService.create(userDto1);
		userService.create(userDto2);
		userService.create(userDto3);
		userService.create(userDto4);
		userService.create(userDto5);
		userService.create(userDto6);

		ResponseEntity<PageResponse<UserDto>> response = restTemplate.exchange(
				"/api/v1/users?name=ja&surname=s&page=0&size=3&sort=email,asc",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {}
		);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(4, response.getBody().getTotalElements());
		assertEquals(2, response.getBody().getTotalPages());
		assertEquals("email1@gmail.com", response.getBody().getContent().get(0).getEmail());
		assertEquals("email2@gmail.com", response.getBody().getContent().get(1).getEmail());
		assertEquals("email5@gmail.com", response.getBody().getContent().get(2).getEmail());
	}

	@Test
	void updateUser(){
		UserDto userDto = new UserDto();
		userDto.setName("Cristiano");
		userDto.setSurname("Ronaldo");
		userDto.setEmail("cristiano@gmail.com");

		userDto = userService.create(userDto);

		userDto.setBirthDate(LocalDate.of(1985, 2, 5));
		userDto.setEmail("suuuuuuuuiiiiiiiii@gmail.ru");

		ResponseEntity<UserDto> response = restTemplate.exchange(
				"/api/v1/users",
				HttpMethod.PUT,
				new HttpEntity<>(userDto),
				UserDto.class);

		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());
		assertEquals(userDto.getName(), response.getBody().getName());
		assertEquals(userDto.getSurname(), response.getBody().getSurname());
		assertEquals(userDto.getBirthDate(), response.getBody().getBirthDate());
		assertEquals(userDto.getEmail(), response.getBody().getEmail());
		assertNotNull(response.getBody().getCreatedAt());
		assertNotNull(response.getBody().getUpdatedAt());
	}

	@Test
	void updateUserById(){
		UserDto userDto = new UserDto();
		userDto.setName("Leonel");
		userDto.setSurname("Messi");
		userDto.setEmail("messi@gmail.com");

		userDto = userService.create(userDto);

		userDto.setBirthDate(LocalDate.of(1987, 6, 24));
		userDto.setEmail("leomessi@gmail.ru");

		ResponseEntity<UserDto> response = restTemplate.exchange(
				"/api/v1/users/" + userDto.getId(),
				HttpMethod.PUT,
				new HttpEntity<>(userDto),
				UserDto.class);

		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());
		assertEquals(userDto.getName(), response.getBody().getName());
		assertEquals(userDto.getSurname(), response.getBody().getSurname());
		assertEquals(userDto.getBirthDate(), response.getBody().getBirthDate());
		assertEquals(userDto.getEmail(), response.getBody().getEmail());
		assertNotNull(response.getBody().getCreatedAt());
		assertNotNull(response.getBody().getUpdatedAt());
	}

	@Test
	void changeUserActivityById(){
		UserDto userDto = new UserDto();
		userDto.setName("Tosin");
		userDto.setSurname("Abasi");
		userDto.setActive(false);
		userDto.setEmail("abasiconcept@gmail.com");

		userDto = userService.create(userDto);

		ResponseEntity<Void> response = restTemplate.exchange(
				"/api/v1/users/" + userDto.getId() + "?active=true",
				HttpMethod.PATCH,
				null,
				Void.class);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

		var user = userRepository.findUserById(userDto.getId()).orElseThrow();
		assertTrue(user.isActive());
	}

	@Test
	void deleteUser() {
		UserDto userDto = new UserDto();
		userDto.setName("Nikolay");
		userDto.setSurname("Baskov");
		userDto.setActive(false);
		userDto.setEmail("zolotyachasha@gmail.com");

		userDto = userService.create(userDto);

		ResponseEntity<Void> response = restTemplate.exchange(
				"/api/v1/users/" + userDto.getId(),
				HttpMethod.DELETE,
				null,
				Void.class
		);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertTrue(userRepository.findUserById(userDto.getId()).isEmpty());
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
