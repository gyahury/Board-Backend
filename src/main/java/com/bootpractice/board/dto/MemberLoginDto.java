package com.bootpractice.board.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MemberLoginDto {

    @NotBlank(message = "email is required")
    @Email(message = "It must be in a valid email format")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;

}
