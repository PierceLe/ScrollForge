package org.scrollSystem.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.UserRequest;
import org.scrollSystem.response.UserResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> getUsers(String email, String username) {
        List<UserResponse> response = new ArrayList<>();

        List<User> userList = userRepository.getListUser(email, username);
        for (User user: userList) {
            UserResponse userResponse = getUserResponse(user);
            response.add(userResponse);
        }

        return response;
    }


    public String delete(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                userRepository::delete,
                () -> {
                    throw new EntityNotFoundException("User with username " + username + " not found");
                }
        );
        return "User with username " + username + " deleted successfully";
    }


    public UserResponse getInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserResponse(user);
    }

    public String checkExistingUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            throw new ValidationException("Username is exist!");
        }

        return "Ok";
    }

    protected UserResponse getUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .uploadNumber(user.getUploadNumber())
                .downloadNumber(user.getDownloadNumber())
                .build();
    }
}