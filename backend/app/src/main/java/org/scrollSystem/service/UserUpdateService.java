package org.scrollSystem.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.UpdatePasswordRequest;
import org.scrollSystem.request.UserUpdateRequest;
import org.scrollSystem.response.UserResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateService {
    final UserRepository userRepository;
    final S3Service s3Service;
    final JwtService jwtService;
    final UserAuthenticationService authenticationService;
    final int maxAttempts = 3;
    private final PasswordEncoder passwordEncoder;
    String firstName;
    String lastName;
    String email;
    String username;
    String password;
    String role;
    String phoneNumber;;

    @Transactional
    public UserResponse update(UserUpdateRequest request, String currUsername) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(user.getUsername(), currUsername)) {
            throw new ValidationException("No editing right");
        }

        //Check for first name
        if (Objects.nonNull(request.getFirstName()))
            user.setFirstName(request.getFirstName());

        // Check for last name
        if (Objects.nonNull(request.getLastName()))
            user.setLastName(request.getLastName());

        // Check for email
        if (Objects.nonNull(request.getEmail()))
            user.setEmail(request.getEmail());


        if (Objects.nonNull(request.getUsername())) {
            // Check for username
            Optional<User> username = userRepository.findByUsername(request.getUsername());

            if (username.isPresent())
                throw new ValidationException("The new username is already taken");
            user.setUsername(request.getUsername());
        }



        // Check for phone number
        if (Objects.nonNull(request.getPhoneNumber()))
            user.setPhone(request.getPhoneNumber());

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    private void setPassword(User user, String password) {
        var salt = user.getSalt();
        user.setPassword(passwordEncoder.encode(password + salt));
    }

    public String updatePassword(String user_id, UpdatePasswordRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(user.getId().toString(), user_id)) {
            throw new ValidationException("No editing right");
        }

        if (!authenticationService.isAuthenticated(user, request.getOldPassword())) {
            throw new ValidationException("Password fail");
        }

        setPassword(user, request.getNewPassword());
        userRepository.save(user);

        return "success";
    }


    public String updateAvatar(MultipartFile file) throws IOException {
        // Check if the file is an image
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String fileUrl = s3Service.uploadFile(file);
        user.setAvatarUrl(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }

}
