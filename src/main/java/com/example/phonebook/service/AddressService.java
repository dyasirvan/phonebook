package com.example.phonebook.service;

import com.example.phonebook.entity.Address;
import com.example.phonebook.model.AddressRequest;
import com.example.phonebook.model.AddressResponse;
import com.example.phonebook.model.AddressSearchRequest;
import com.example.phonebook.model.PagingResponse;
import com.example.phonebook.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressResponse create(AddressRequest addressRequest){
        // mapping request to entity
        Address address = new Address();
        address.setCity(addressRequest.getCity());
        address.setProvince(addressRequest.getProvince());
        address.setCountry(addressRequest.getCountry());
        address.setPostalCode(addressRequest.getPostalCode());

        // save to database
        Address addressSaved = addressRepository.save(address);

        // mapping entity to response
        return toContactResponse(addressSaved);

    }

    public PagingResponse<AddressResponse> search(AddressSearchRequest addressSearchRequest){
        Specification<Address> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(addressSearchRequest.getCity())) {
                predicates.add(builder.like(root.get("city"), "%" + addressSearchRequest.getCity() + "%"));
            }
            if (Objects.nonNull(addressSearchRequest.getProvince())) {
                predicates.add(builder.like(root.get("phone"), "%" + addressSearchRequest.getProvince() + "%"));
            }
            if (Objects.nonNull(addressSearchRequest.getCountry())) {
                predicates.add(builder.like(root.get("country"), "%" + addressSearchRequest.getCountry() + "%"));
            }
            if (Objects.nonNull(addressSearchRequest.getPostalCode())) {
                predicates.add(builder.like(root.get("postalCode"), "%" + addressSearchRequest.getPostalCode() + "%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(addressSearchRequest.getPage(), addressSearchRequest.getSize());
        Page<Address> addresses = addressRepository.findAll(specification, pageable);
        List<AddressResponse> contactResponses = addresses.getContent().stream()
                .map(this::toContactResponse)
                .toList();

        PageImpl<AddressResponse> addressResponsePage = new PageImpl<>(contactResponses, pageable, addresses.getTotalElements());
        return PagingResponse.<AddressResponse>builder()
                .data(addressResponsePage.getContent())
                .size(addressResponsePage.getSize())
                .currentPage(addressResponsePage.getNumber())
                .totalPage(addressResponsePage.getTotalPages())
                .build();

    }

    public AddressResponse get(Long id){
        Address address = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Address not found"));
        return toContactResponse(address);
    }

    public AddressResponse update(Long id, AddressRequest addressRequest){
        Address address = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Address not found"));
        address.setCity(addressRequest.getCity());
        address.setProvince(addressRequest.getProvince());
        address.setCountry(addressRequest.getCountry());
        address.setPostalCode(addressRequest.getPostalCode());

        Address addressSaved = addressRepository.save(address);
        return toContactResponse(addressSaved);
    }

    public void delete(Long id){
        Address address = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    private AddressResponse toContactResponse(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
