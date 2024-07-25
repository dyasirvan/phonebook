package com.example.phonebook.controller;

import com.example.phonebook.model.ContactRequest;
import com.example.phonebook.model.ContactResponse;
import com.example.phonebook.model.ContactSearchRequest;
import com.example.phonebook.model.PagingResponse;
import com.example.phonebook.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Validated
public class ContactController {

    private final ContactService contactService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Validated ContactRequest request){
        contactService.create(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagingResponse<ContactResponse>> get(@Validated ContactSearchRequest request){
        return new ResponseEntity<>(contactService.search(request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> get(@PathVariable Long id){
        return new ResponseEntity<>(contactService.get(id), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContactResponse> update(@PathVariable Long id, @RequestBody @Validated ContactRequest request){
        return new ResponseEntity<>(contactService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        contactService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
