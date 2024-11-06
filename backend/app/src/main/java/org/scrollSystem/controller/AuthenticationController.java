package org.scrollSystem.controller;


import jakarta.validation.Valid;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.RegisterRequest;
import org.scrollSystem.response.AuthenticationResponse;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserAuthenticationService userAuthenticationService;


    // API for registering an account
    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<UserResponse>> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        try {
            UserResponse userResponse = userAuthenticationService.register(request);
            return DefaultResponse.success(userResponse);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }

    // API for login account
    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            AuthenticationResponse response = userAuthenticationService.login(request);
            return DefaultResponse.success(response);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }
}
