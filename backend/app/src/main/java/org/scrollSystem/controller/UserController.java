package org.scrollSystem.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.scrollSystem.models.User;
import org.scrollSystem.response.DefaultListResponse;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    // API FOR USER MANAGEMENT
    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DefaultListResponse<UserResponse>> getUsers(
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String username
    ) {
        return DefaultListResponse.success(userService.getUsers(email, username));
    }

    // API for delete user
    @DeleteMapping("/{usernamme}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DefaultResponse<String>> deleteUser (
            @PathVariable @NotNull String usernamme
    ) {
        try {
            String response = userService.delete(usernamme);
            return DefaultResponse.success(response);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }

    // APi for get info of user from JWT
    @GetMapping("/info")
    public ResponseEntity<DefaultResponse<UserResponse>> register(
    ) {
        return DefaultResponse.success(userService.getInfo());
    }

    // API for checking exist username or not
    @GetMapping("/check-username")
    public ResponseEntity<DefaultResponse<String>> checkExistingUsername(
            @RequestParam String username
    ) {
        try {
            String response = userService.checkExistingUsername(username);
            return DefaultResponse.success(response);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }
}