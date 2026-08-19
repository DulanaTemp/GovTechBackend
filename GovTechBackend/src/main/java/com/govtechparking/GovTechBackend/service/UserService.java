package com.govtechparking.GovTechBackend.service;

import com.govtechparking.GovTechBackend.dto.user.UserResponse;
import com.govtechparking.GovTechBackend.entity.User;
import com.govtechparking.GovTechBackend.exception.ResourceNotFoundException;
import com.govtechparking.GovTechBackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return UserResponse.from(getUserOrThrow(id));
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
