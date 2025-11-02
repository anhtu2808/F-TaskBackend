package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.request.address.AddressRequest;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import com.anhtu.ftaskbackend.entity.Address;
import com.anhtu.ftaskbackend.entity.Customer;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.AddressMapper;
import com.anhtu.ftaskbackend.repository.AddressRepository;
import com.anhtu.ftaskbackend.repository.CustomerRepository;
import com.anhtu.ftaskbackend.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class AddressServiceImpl implements AddressService {

    AddressRepository addressRepository;
    CustomerRepository customerRepository;
    AddressMapper mapper;

    @Override
    public AddressResponse create(AddressRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                                              .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Address entity = mapper.toEntity(request);
        entity.setCustomer(customer);

        addressRepository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public AddressResponse update(Long id, AddressRequest request) {
        Address entity = addressRepository.findById(id)
                                          .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));

        mapper.updateEntityFromRequest(request, entity);
        addressRepository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public AddressResponse getById(Long id) {
        Address entity = addressRepository.findById(id)
                                          .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));
        return mapper.toResponse(entity);
    }

    @Override
    public List<AddressResponse> getAllByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new AppException(ErrorCode.CustomerNotFound);
        }

        return addressRepository.findByCustomerId(customerId)
                                .stream()
                                .map(mapper::toResponse)
                                .toList();
    }

    @Override
    public void delete(Long id) {
        Address entity = addressRepository.findById(id)
                                          .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));
        addressRepository.delete(entity);
    }
}
