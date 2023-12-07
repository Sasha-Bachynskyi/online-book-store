package com.onlinebookstore.dto.user;

import com.onlinebookstore.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatch
public class UserRegistrationRequestDto {
    @NotNull
    @Email
    private String email;
    @Size(min = 8, max = 50)
    private String password;
    @Size(min = 8, max = 50)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
