package com.example.phonebook.controller;

import com.example.phonebook.entity.Address;
import com.example.phonebook.entity.Contact;
import com.example.phonebook.entity.User;
import com.example.phonebook.model.*;
import com.example.phonebook.repository.AddressRepository;
import com.example.phonebook.repository.ContactRepository;
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
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestService guestService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        contactRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    private static RequestPostProcessor bearerToken(String token) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    private Address saveAddress(){
        Address address = new Address();
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");

        return addressRepository.save(address);
    }

    private User saveUser(){
        User user = new User();
        user.setEmail("dyas@yopmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));

        return userRepository.save(user);
    }

    private String generateToken(){
        User user = saveUser();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword("12345678");
        LoginResponse response = guestService.login(loginRequest);

        return response.getToken();
    }

    @Test
    void createSuccess() throws Exception{
        ContactRequest request = new ContactRequest();
        request.setName("Dyas");
        request.setEmail("dyas@yopmail.com");
        request.setPhone("0812345678");
        request.setStreet("Jl. Jalan");

        mockMvc.perform(post("/api/contacts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    void createFailed() throws Exception{
        ContactRequest request = new ContactRequest();

        mockMvc.perform(post("/api/contacts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    void searchSuccess() throws Exception{

        String token = generateToken();

        Contact contact = new Contact();
        contact.setName("Dyas");
        contact.setEmail("dyasirvan@yopmail.com");
        contact.setPhone("0812345678");
        contact.setStreet("Jl. Jalan");
        contact.setUser(userRepository.findByEmail("dyas@yopmail.com").orElse(null));
        contact.setAddress(saveAddress());

        contactRepository.save(contact);

        mockMvc.perform(get("/api/contacts")
                        .param("name", "Dyas")
                        .with(bearerToken(token)))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    PagingResponse<ContactResponse> contactResponsePagingResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals(1, contactResponsePagingResponse.getData().size());
                    assertEquals("Dyas", contactResponsePagingResponse.getData().get(0).getName());
                    assertEquals(0, contactResponsePagingResponse.getCurrentPage());
                    assertEquals(1, contactResponsePagingResponse.getTotalPage());
                    assertEquals(10, contactResponsePagingResponse.getSize());
                });
    }

    @Test
    void searchEmpty() throws Exception{

        mockMvc.perform(get("/api/contacts")
                        .param("name", "Dyas")
                        .with(bearerToken(generateToken())))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    PagingResponse<ContactResponse> contactResponsePagingResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals(0, contactResponsePagingResponse.getData().size());
                    assertEquals(0, contactResponsePagingResponse.getCurrentPage());
                    assertEquals(0, contactResponsePagingResponse.getTotalPage());
                    assertEquals(10, contactResponsePagingResponse.getSize());
                });
    }

    @Test
    void getByIdSuccess()throws Exception{
        String token = generateToken();

        Contact contact = new Contact();
        contact.setName("Dyas");
        contact.setEmail("dyasirvan@yopmail.com");
        contact.setPhone("0812345678");
        contact.setStreet("Jl. Jalan");
        contact.setUser(userRepository.findByEmail("dyas@yopmail.com").orElse(null));
        contact.setAddress(saveAddress());

        Contact savedContact = contactRepository.save(contact);

        mockMvc.perform(get("/api/contacts/" + savedContact.getId())
                        .with(bearerToken(token)))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    ContactResponse contactResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals("Dyas", contactResponse.getName());
                });
    }

    @Test
    void getByIdNotFound(){
        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/contacts/1")
                            .with(bearerToken(generateToken())));
        });
    }

    @Test
    void updateSuccess()throws Exception{
        String token = generateToken();
        Address savedAddress = saveAddress();

        Contact contact = new Contact();
        contact.setName("Dyas");
        contact.setEmail("dyasirvan@yopmail.com");
        contact.setPhone("0812345678");
        contact.setStreet("Jl. Jalan");
        contact.setUser(userRepository.findByEmail("dyas@yopmail.com").orElse(null));
        contact.setAddress(savedAddress);

        Contact savedContact = contactRepository.save(contact);

        ContactRequest request = new ContactRequest();
        request.setName("Irvan");
        request.setEmail("dyasirvan@yopmail.com");
        request.setPhone("0812345678");
        request.setStreet("Jl. Jalan");
        request.setAddressId(savedAddress.getId());

        mockMvc.perform(put("/api/contacts/" + savedContact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(bearerToken(token)))
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    ContactResponse contactResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals("Irvan", contactResponse.getName());
                });
    }

    @Test
    void updateFailed() {
        String token = generateToken();
        Address savedAddress = saveAddress();

        User user = new User();
        user.setEmail("test@yopmail.com");

        User savedUser = userRepository.save(user);

        Contact contact = new Contact();
        contact.setName("Dyas");
        contact.setEmail("dyasirvan@yopmail.com");
        contact.setPhone("0812345678");
        contact.setStreet("Jl. Jalan");
        contact.setUser(savedUser);
        contact.setAddress(savedAddress);

        Contact savedContact = contactRepository.save(contact);

        ContactRequest request = new ContactRequest();
        request.setName("Irvan");
        request.setEmail("dyasirvan@yopmail.com");
        request.setPhone("0812345678");
        request.setStreet("Jl. Jalan");
        request.setAddressId(savedAddress.getId());

        assertThrows(Exception.class, () -> {
            mockMvc.perform(put("/api/contacts/" + savedContact.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
                            .with(bearerToken(token)))
                    .andExpectAll(
                            status().isNotFound()
                    );
        });
    }

    @Test
    void deleteSuccess() throws Exception {
        String token = generateToken();
        Address savedAddress = saveAddress();

        Contact contact = new Contact();
        contact.setName("Dyas");
        contact.setEmail("dyasirvan@yopmail.com");
        contact.setPhone("0812345678");
        contact.setStreet("Jl. Jalan");
        contact.setUser(userRepository.findByEmail("dyas@yopmail.com").orElse(null));
        contact.setAddress(savedAddress);

        Contact savedContact = contactRepository.save(contact);

        ContactRequest request = new ContactRequest();
        request.setName("Irvan");
        request.setEmail("dyasirvan@yopmail.com");
        request.setPhone("0812345678");
        request.setStreet("Jl. Jalan");
        request.setAddressId(savedAddress.getId());

        mockMvc.perform(delete("/api/contacts/" + savedContact.getId())
                        .with(bearerToken(token)))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    void deleteFailed() {
        assertThrows(Exception.class, () -> {
            mockMvc.perform(delete("/api/contacts/1")
                            .with(bearerToken(generateToken())))
                    .andExpectAll(
                            status().isNotFound()
                    );
        });
    }

}