package com.govtechparking.GovTechBackend.controller;

import com.govtechparking.GovTechBackend.dto.user.UserResponse;
import com.govtechparking.GovTechBackend.security.CurrentUser;
import com.govtechparking.GovTechBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Authenticated user profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's profile")
    public UserResponse me() {
        return userService.findById(CurrentUser.id());
    }
}
