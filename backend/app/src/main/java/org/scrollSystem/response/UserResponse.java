package org.scrollSystem.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String role;
    private String phone;
    private String avatarUrl;
    private Integer uploadNumber;
    private Integer downloadNumber;
}