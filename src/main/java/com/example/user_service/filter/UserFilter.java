package com.example.user_service.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFilter {
    private String name;
    private String surname;
}
