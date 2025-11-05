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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class AddressServiceImpl implements AddressService {

    AddressRepository addressRepository;
    CustomerRepository customerRepository;
    AddressMapper mapper;

    @Override
    public List<AddressResponse> getAllByCurrentUser(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        return addressRepository.findByCustomerId(customer.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse create(Long userId, AddressRequest request) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Address entity = mapper.toEntity(request);
        entity.setCustomer(customer);

        List<Address> existingAddresses = addressRepository.findByCustomerId(customer.getId());
        
        if (existingAddresses.isEmpty()) {
            entity.setIsDefault(true);
        } else if (Boolean.TRUE.equals(request.getIsDefault())) {
            setAllAddressesNotDefault(customer.getId());
        }

        addressRepository.save(entity);
        log.info("Created address {} for customer {}", entity.getId(), customer.getId());
        return mapper.toResponse(entity);
    }

    @Override
    public AddressResponse update(Long userId, Long addressId, AddressRequest request) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Address entity = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));

        // Kiểm tra address có thuộc về customer này không
        if (!entity.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Nếu set isDefault = true, set tất cả address khác thành false
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            setAllAddressesNotDefault(customer.getId());
        }

        mapper.updateEntityFromRequest(request, entity);
        addressRepository.save(entity);

        log.info("Updated address {} for customer {}", addressId, customer.getId());
        return mapper.toResponse(entity);
    }

    @Override
    public AddressResponse getById(Long userId, Long addressId) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Address entity = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));

        // Kiểm tra address có thuộc về customer này không
        if (!entity.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long userId, Long addressId) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CustomerNotFound));

        Address entity = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.AddressNotFound));

        // Kiểm tra address có thuộc về customer này không
        if (!entity.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        addressRepository.delete(entity);
        log.info("Deleted address {} for customer {}", addressId, customer.getId());
    }

    private void setAllAddressesNotDefault(Long customerId) {
        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        addresses.forEach(addr -> addr.setIsDefault(false));
        addressRepository.saveAll(addresses);
    }
}
