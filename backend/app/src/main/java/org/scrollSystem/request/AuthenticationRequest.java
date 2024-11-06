package org.scrollSystem.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "email can not be empty")
    @NotBlank(message = "email can not be empty")
    private String username;

    @NotNull(message = "password can not be empty")
    @NotBlank(message = "password can not be empty")
    private String password;
}
