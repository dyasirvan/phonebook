package com.example.phonebook.controller;

import com.example.phonebook.model.LoginRequest;
import com.example.phonebook.model.LoginResponse;
import com.example.phonebook.model.RegisterRequest;
import com.example.phonebook.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class GuestController {

    private final GuestService guestService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterRequest request) {
        guestService.register(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        return new ResponseEntity<>(guestService.login(request), HttpStatus.OK);
    }

}
