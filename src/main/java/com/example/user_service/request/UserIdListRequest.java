package com.example.user_service.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record UserIdListRequest(@NotBlank List<UUID> userIdList) {
}
