package com.example.phonebook.controller;

import com.example.phonebook.model.AddressRequest;
import com.example.phonebook.model.AddressResponse;
import com.example.phonebook.model.AddressSearchRequest;
import com.example.phonebook.model.PagingResponse;
import com.example.phonebook.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressResponse> create(@RequestBody @Validated AddressRequest addressRequest) {
        return new ResponseEntity<>(addressService.create(addressRequest), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagingResponse<AddressResponse>> search(@Validated AddressSearchRequest addressSearchRequest) {
        return new ResponseEntity<>(addressService.search(addressSearchRequest), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AddressResponse> get(@PathVariable Long id) {
        return new ResponseEntity<>(addressService.get(id), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @RequestBody @Validated AddressRequest addressRequest) {
        return new ResponseEntity<>(addressService.update(id, addressRequest), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
