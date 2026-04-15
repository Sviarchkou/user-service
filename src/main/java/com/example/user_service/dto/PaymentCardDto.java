package com.example.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCardDto implements Serializable {

    @NotNull(message = "ID is required", groups = UpdateGroup.class)
    UUID id;

    @NotNull
    UUID userId;

    @Size(min = 16, max = 16, message = "Payment card number must be 16 digits long")
    @Pattern(regexp = "\\d{16}", message = "Payment card number must contain only digits")
    String number;

    @NotBlank
    @Size(max = 255)
    String holder;

    @DateTimeFormat(pattern = "yyyy-MM-DD")
    //@JsonFormat(pattern = )
    LocalDate expirationDate;

    boolean active = false;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    public interface UpdateGroup {}
}
