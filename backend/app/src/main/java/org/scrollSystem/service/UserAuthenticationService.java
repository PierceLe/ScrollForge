package org.scrollSystem.service;

import org.scrollSystem.config.ApplicationConfig;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.RegisterRequest;
import org.scrollSystem.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.scrollSystem.response.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationConfig applicationConfig;

    public UserResponse register(RegisterRequest request) throws IOException {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            throw new ValidationException("Email have been existed");
        }

        Optional<User> optionalUserByName = userRepository.findByUsername(request.getUsername());
        if (optionalUserByName.isPresent()) {
            throw new ValidationException("Username have been existed");
        }

        String salt = Base64.getEncoder().encodeToString(applicationConfig.getNextSalt());
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword() + salt))
                .salt(salt)
                .role("ROLE_USER")
                .phone(request.getPhone())
                .build();
        System.out.println(request.getPhone());
        userRepository.save(user);
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .phone(user.getPhone())
                .build();
        return userResponse;
    }


    public AuthenticationResponse login(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();


        // Extract the hash
        var hash = user.getSalt();
        var hashInputPassword = passwordEncoder.encode(request.getPassword() + hash);

        // Check the hash input
        if (!hashInputPassword.equals(user.getPassword()))
            throw new ValidationException("Wrong Password");


        // Generate a token if the user logins successfully
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public boolean isAuthenticated(User user, String password) {
        var hash = user.getSalt();
        var hashInputPassword = passwordEncoder.encode(password + hash);

        // Check the hash input
        if (!hashInputPassword.equals(user.getPassword())) {
            return false;
        }
        return true;
    }
}
