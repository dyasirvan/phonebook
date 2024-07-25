package com.example.phonebook.model;

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
public class AddressRequest {

    @NotNull
    private String city;

    @NotNull
    private String province;

    @NotNull
    private String country;

    @NotNull
    @Size(max = 10)
    private String postalCode;

}
