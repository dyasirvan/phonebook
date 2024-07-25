package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactSearchRequest extends SearchPageRequest {

    private String name;

    private String phone;

    private String email;

    private String street;

}
