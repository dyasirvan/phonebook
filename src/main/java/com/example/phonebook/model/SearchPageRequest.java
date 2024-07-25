package com.example.phonebook.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPageRequest {
    @NotNull
    @Builder.Default
    private Integer page = 0;

    @NotNull
    @Builder.Default
    private Integer size = 10;

}
