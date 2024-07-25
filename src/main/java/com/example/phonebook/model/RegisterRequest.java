package com.example.phonebook.model;

import com.example.phonebook.validation.constraint.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull
    @Email
    @UniqueEmail
    private String email;

    @NotNull
    @Size(min = 8, max = 100)
    private String password;
}
