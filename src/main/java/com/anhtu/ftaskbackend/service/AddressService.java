package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.address.AddressRequest;
import com.anhtu.ftaskbackend.dto.response.address.AddressResponse;
import java.util.List;

public interface AddressService {
    AddressResponse create(Long userId, AddressRequest request);
    AddressResponse update(Long userId, Long addressId, AddressRequest request);
    void delete(Long userId, Long addressId);
    AddressResponse getById(Long userId, Long addressId);
    List<AddressResponse> getAllByCurrentUser(Long userId);
}
