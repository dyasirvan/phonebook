package com.example.phonebook.controller;

import com.example.phonebook.entity.User;
import com.example.phonebook.model.ErrorResponse;
import com.example.phonebook.model.LoginRequest;
import com.example.phonebook.model.LoginResponse;
import com.example.phonebook.model.RegisterRequest;
import com.example.phonebook.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("dyas@yopmail.com");
        registerRequest.setPassword("12345678");

        mockMvc.perform(post("/api/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    void registerFailedEmailExist() throws Exception{
        String email = "irvan@yopmail.com";
        String password = "12345678";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(user.getEmail());
        registerRequest.setPassword(password);

        mockMvc.perform(post("/api/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
                });
    }

    @Test
    void registerFailedPasswordLength() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("dyas@yopmail.com");
        registerRequest.setPassword("123");

        mockMvc.perform(post("/api/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
                });
    }

    @Test
    void loginSuccess() throws Exception {
        String email = "dyas@yopmail.com";
        String password = "12345678";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
                    assertFalse(response.getToken().isEmpty());
                });
    }

    @Test
    void loginFailedEmailNotFound() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("dyas@yopmail.com");
        loginRequest.setPassword("12345678");

        mockMvc.perform(post("/api/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
                    assertEquals("invalid email or password", response.getMessage());
                });
    }

    @Test
    void loginFailedPasswordWrong() throws Exception {
        String email = "dyas@yopmail.com";
        String password = "12345678";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("123");

        mockMvc.perform(post("/api/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
                    assertEquals("invalid email or password", response.getMessage());
                });
    }
}