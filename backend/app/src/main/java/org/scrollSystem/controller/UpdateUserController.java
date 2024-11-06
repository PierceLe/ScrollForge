package org.scrollSystem.controller;


import jakarta.validation.Valid;
import lombok.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.scrollSystem.request.UpdatePasswordRequest;
import org.scrollSystem.request.UserUpdateRequest;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.service.UserUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/update")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateUserController {
    UserUpdateService userUpdateService;

    // API for update user information like lastName, ...
    @PutMapping("/user/{user_id}")
    public ResponseEntity<DefaultResponse<UserResponse>> update(@RequestBody @Valid UserUpdateRequest request,
                                               @PathVariable String user_id) {
        try {
            UserResponse response = userUpdateService.update(request, user_id);
            return DefaultResponse.success(response);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }

    // API for update the password
    @PutMapping("password/{user_id}")
    public ResponseEntity<DefaultResponse<String>> updatePassword(
            @PathVariable String user_id,
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        try {
            String response = userUpdateService.updatePassword(user_id, request);
            return DefaultResponse.success(response);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }

    // API for update avatar
    @PutMapping("/avatar")
    public ResponseEntity<DefaultResponse<String>> updateAvatar(
            @RequestParam MultipartFile file
    ) {
        try {
            String response = userUpdateService.updateAvatar(file);
            return DefaultResponse.success(response);
        }
        catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }
}
