package com.example.phonebook.service;

import com.example.phonebook.entity.User;
import com.example.phonebook.model.LoginRequest;
import com.example.phonebook.model.LoginResponse;
import com.example.phonebook.model.RegisterRequest;
import com.example.phonebook.repository.UserRepository;
import com.example.phonebook.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request){
        // create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // save user
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest loginRequest){
        try{
            // authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // get user by email
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("invalid email or password"));

            // set claims userId
            Map<String, Object> claims = Map.of("userId", user.getId());

            // return token
            return LoginResponse.builder()
                    .token(jwtService.createToken(claims, loginRequest.getEmail()))
                    .build();
        }catch (UsernameNotFoundException | BadCredentialsException e){
            // throw exception if user not found or invalid password
            throw new UsernameNotFoundException("invalid email or password");
        }
    }
}
