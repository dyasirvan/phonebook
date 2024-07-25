package com.example.phonebook.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactRequest {

    @NotNull
    private String name;

    @NotNull
    @Size(min = 10, max = 13)
    private String phone;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String street;

    private Long addressId;

}
