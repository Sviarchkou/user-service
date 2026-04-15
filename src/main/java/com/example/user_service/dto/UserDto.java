package com.example.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto implements Serializable {

    @NotNull(message = "ID is required", groups = UpdateGroup.class)
    UUID id;

    @NotBlank
    @Length(min = 2, max = 150, message = "Name size must be between  2 and 150 characters")
    String name;

    @NotBlank
    @Length(min = 2, max = 150, message = "Surname size must be between  2 and 150 characters")
    String surname;

    LocalDate birthDate;

    @NotBlank
    @Email(message = "String does not match the email format")
    String email;

    boolean active = false;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    public interface UpdateGroup {}

}
