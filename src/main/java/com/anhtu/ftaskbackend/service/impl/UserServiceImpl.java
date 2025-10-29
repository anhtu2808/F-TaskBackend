package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.entity.User;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return null;
    }
}
