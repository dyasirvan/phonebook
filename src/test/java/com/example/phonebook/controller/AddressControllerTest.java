package com.example.phonebook.controller;

import com.example.phonebook.entity.Address;
import com.example.phonebook.entity.User;
import com.example.phonebook.model.*;
import com.example.phonebook.repository.AddressRepository;
import com.example.phonebook.repository.UserRepository;
import com.example.phonebook.service.GuestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestService guestService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addAddressSuccess() throws Exception {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("Jakarta");
        addressRequest.setProvince("DKI Jakarta");
        addressRequest.setCountry("Indonesia");
        addressRequest.setPostalCode("12345");

        mockMvc.perform(post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(bearerToken(generateToken()))
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    AddressResponse addressResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AddressResponse.class);
                    assertNotNull(addressResponse.getId());
                    assertEquals(addressRequest.getCity(), addressResponse.getCity());
                    assertEquals(addressRequest.getProvince(), addressResponse.getProvince());
                    assertEquals(addressRequest.getCountry(), addressResponse.getCountry());
                    assertEquals(addressRequest.getPostalCode(), addressResponse.getPostalCode());
                });
    }

    @Test
    void addAddressFailedUnauthorized() throws Exception {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("Jakarta");
        addressRequest.setProvince("DKI Jakarta");
        addressRequest.setCountry("Indonesia");
        addressRequest.setPostalCode("12345");

        mockMvc.perform(post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void addAddressFailedBadRequest() throws Exception {
        AddressRequest addressRequest = new AddressRequest();

        mockMvc.perform(post("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(bearerToken(generateToken()))
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    void searchAddressSuccess() throws Exception{
        for(int i = 0; i < 20; i++){
            Address address = new Address();
            address.setCity("Jakarta");
            address.setProvince("DKI Jakarta");
            address.setCountry("Indonesia");
            address.setPostalCode("12345" + i);

            addressRepository.save(address);
        }

        mockMvc.perform(get("/api/addresses")
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    PagingResponse<AddressResponse> pagingResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals(10, pagingResponse.getData().size());
                    assertEquals(0, pagingResponse.getCurrentPage());
                    assertEquals(2, pagingResponse.getTotalPage());
                    assertEquals(10, pagingResponse.getSize());
                });
    }

    @Test
    void searchAddressNull() throws Exception{
        mockMvc.perform(get("/api/addresses")
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    PagingResponse<AddressResponse> pagingResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals(0, pagingResponse.getData().size());
                    assertEquals(0, pagingResponse.getCurrentPage());
                    assertEquals(0, pagingResponse.getTotalPage());
                    assertEquals(10, pagingResponse.getSize());
                });
    }

    @Test
    void getByIdSuccess() throws Exception {
        Address address = new Address();
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");

        Address addressSaved = addressRepository.save(address);

        mockMvc.perform(get("/api/addresses/" + addressSaved.getId())
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    AddressResponse addressResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AddressResponse.class);
                    assertEquals(addressSaved.getId(), addressResponse.getId());
                    assertEquals(addressSaved.getCity(), addressResponse.getCity());
                    assertEquals(addressSaved.getProvince(), addressResponse.getProvince());
                    assertEquals(addressSaved.getCountry(), addressResponse.getCountry());
                    assertEquals(addressSaved.getPostalCode(), addressResponse.getPostalCode());
                });
    }

    @Test
    void getByIdEmpty(){
        assertThrows(Exception.class, () -> mockMvc.perform(get("/api/addresses/1")
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isNotFound()
                ));
    }

    @Test
    void updateSuccess() throws Exception{
        Address address = new Address();
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");

        Address addressSaved = addressRepository.save(address);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("Bandung");
        addressRequest.setProvince("Jawa Barat");
        addressRequest.setCountry("Indonesia");
        addressRequest.setPostalCode("54321");

        mockMvc.perform(put("/api/addresses/" + addressSaved.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(bearerToken(generateToken()))
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    AddressResponse addressResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AddressResponse.class);
                    assertEquals(addressSaved.getId(), addressResponse.getId());
                    assertEquals(addressRequest.getCity(), addressResponse.getCity());
                    assertEquals(addressRequest.getProvince(), addressResponse.getProvince());
                    assertEquals(addressRequest.getCountry(), addressResponse.getCountry());
                    assertEquals(addressRequest.getPostalCode(), addressResponse.getPostalCode());
                });
    }

    @Test
    void updateFailed() throws Exception{
        Address address = new Address();
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");

        Address addressSaved = addressRepository.save(address);

        AddressRequest addressRequest = new AddressRequest();

        mockMvc.perform(put("/api/addresses/" + addressSaved.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(bearerToken(generateToken()))
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNotNull(errorResponse);
                });
    }

    @Test
    void deleteSuccess(){
        Address address = new Address();
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");

        Address addressSaved = addressRepository.save(address);

        assertDoesNotThrow(() -> mockMvc.perform(delete("/api/addresses/" + addressSaved.getId())
                        .with(bearerToken(generateToken()))
                ).andExpectAll(
                        status().isOk()
                ));

    }

    @Test
    void deleteFailed() {
        assertThrows(Exception.class, () -> mockMvc.perform(delete("/api/addresses/1")
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isNotFound()
                ));
    }

    private static RequestPostProcessor bearerToken(String token) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    private String generateToken(){
        String password = "12345678";
        User user = new User();
        user.setEmail("dyas@yopmail.com");
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(password);
        LoginResponse response = guestService.login(loginRequest);

        return response.getToken();
    }

}