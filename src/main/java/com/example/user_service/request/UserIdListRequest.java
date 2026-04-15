package com.example.user_service.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UserIdListRequest(@NotNull List<UUID> userIdList) {
}
