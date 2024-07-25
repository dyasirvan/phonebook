package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressSearchRequest extends SearchPageRequest {
    private String city;
    private String postalCode;
    private String province;
    private String country;
}
