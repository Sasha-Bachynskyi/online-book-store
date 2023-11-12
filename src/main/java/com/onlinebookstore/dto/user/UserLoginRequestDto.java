package com.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDto {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 8, max = 50)
    private String password;
}
