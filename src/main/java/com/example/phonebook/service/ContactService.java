package com.example.phonebook.service;

import com.example.phonebook.entity.Contact;
import com.example.phonebook.model.*;
import com.example.phonebook.repository.AddressRepository;
import com.example.phonebook.repository.ContactRepository;
import com.example.phonebook.repository.UserRepository;
import com.example.phonebook.service.jwt.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    public void create(ContactRequest request){
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setStreet(request.getStreet());

        // check addressId is present or not
        // if present, find the address by id and set it to contact
        // if address not present, set address to null
        Optional.ofNullable(request.getAddressId())
                .flatMap(addressRepository::findById)
                .ifPresent(contact::setAddress);

        // Extract token from request header
        String token = jwtService.resolveToken(this.request);
        if (token != null) {
            // Extract userId from token
            Long userId = jwtService.getUserIdFromToken(token);
            // Find user by userId and set to contact
            userRepository.findById(userId).ifPresent(contact::setUser);
        }

        contactRepository.save(contact);
    }

    public PagingResponse<ContactResponse> search(ContactSearchRequest contactSearchRequest){
        // Extract token from request header
        String token = jwtService.resolveToken(this.request);
        // Extract userId from token
        Long userId = jwtService.getUserIdFromToken(token);

        Specification<Contact> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add condition to filter by userId
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if(Objects.nonNull(contactSearchRequest.getName())){
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + contactSearchRequest.getName() + "%"));
            }
            if(Objects.nonNull(contactSearchRequest.getEmail())){
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + contactSearchRequest.getEmail() + "%"));
            }
            if(Objects.nonNull(contactSearchRequest.getPhone())){
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + contactSearchRequest.getPhone() + "%"));
            }
            if(Objects.nonNull(contactSearchRequest.getStreet())){
                predicates.add(criteriaBuilder.like(root.get("street"), "%" + contactSearchRequest.getStreet() + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };

        Pageable pageable = PageRequest.of(contactSearchRequest.getPage(), contactSearchRequest.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(this::mapToResponse).toList();

        PageImpl<ContactResponse> contactResponsePage = new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
        return PagingResponse.<ContactResponse>builder()
                .data(contactResponsePage.getContent())
                .size(contactResponsePage.getSize())
                .currentPage(contactResponsePage.getNumber())
                .totalPage(contactResponsePage.getTotalPages())
                .build();
    }

    public ContactResponse get(Long id){
        return contactRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found"));
    }

    public ContactResponse update(Long id, ContactRequest contactRequest){
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found"));

        // validate userId from contact is equal from header
        this.validateUserId(contact.getUser().getId());
        contact.setName(contactRequest.getName());
        contact.setEmail(contactRequest.getEmail());
        contact.setPhone(contactRequest.getPhone());
        contact.setStreet(contactRequest.getStreet());

        // check addressId is present or not
        // if present, find the address by id and set it to contact
        // if address not present, set address to null
        Optional.ofNullable(contactRequest.getAddressId())
                .flatMap(addressRepository::findById)
                .ifPresent(contact::setAddress);

        contactRepository.save(contact);
        return mapToResponse(contact);
    }

    public void delete(Long id){
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found"));

        // validate userId from contact is equal from header
        this.validateUserId(contact.getUser().getId());

        contactRepository.delete(contact);
    }

    private void validateUserId(Long userId){
        String token = jwtService.resolveToken(this.request);
        Long userIdFromToken = jwtService.getUserIdFromToken(token);
        if(!userId.equals(userIdFromToken)){
            throw new EntityNotFoundException("Contact not found");
        }
    }

    private ContactResponse mapToResponse(Contact contact){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(contact.getUser().getId());
        userResponse.setEmail(contact.getUser().getEmail());

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setId(contact.getAddress().getId());
        addressResponse.setCity(contact.getAddress().getCity());
        addressResponse.setProvince(contact.getAddress().getProvince());
        addressResponse.setCountry(contact.getAddress().getCountry());
        addressResponse.setPostalCode(contact.getAddress().getPostalCode());

        ContactResponse response = new ContactResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setEmail(contact.getEmail());
        response.setPhone(contact.getPhone());
        response.setStreet(contact.getStreet());
        response.setUser(userResponse);
        response.setAddress(addressResponse);
        return response;
    }
}
