package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String street;
    private AddressResponse address;
    private UserResponse user;
}
