package com.example.user_service.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationFilter {
    private String name;
    private String surname;
    private Boolean active;
}
