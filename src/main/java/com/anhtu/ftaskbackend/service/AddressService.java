package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.address.AddressRequest;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import java.util.List;

public interface AddressService {
    AddressResponse create(AddressRequest request);
    AddressResponse update(Long id, AddressRequest request);
    void delete(Long id);
    AddressResponse getById(Long id);
    List<AddressResponse> getAllByCustomer(Long customerId);
}
