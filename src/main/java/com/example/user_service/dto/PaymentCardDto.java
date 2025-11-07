package com.example.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCardDto {
    @NotNull
    UUID id;

    @NotNull
    UUID userId;

    @Size(min = 16, max = 16, message = "Payment card number must be 16 digits long")
    @Pattern(regexp = "\\d{16}", message = "Payment card number must contain only digits")
    String number;

    @NotBlank
    @Size(max = 255)
    String holder;

    @Pattern(
            regexp = "^(0[1-9]|1[0-2])/\\d{2}$",
            message = "Expiration date must be in format MM/YY, with month from 01 to 12"
    )
    String expirationDate;

    boolean active = false;

    @NotNull
    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
