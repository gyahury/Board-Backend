package com.bootpractice.board.dto;

import com.bootpractice.board.domain.Member;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MemberJoinDto {
    @NotBlank(message = "email is required")
    @Email(message = "It must be in a valid email format")
    private String email;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "nickname is required")
    private String nickname;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;

    public Member toEntity() {
        return new Member(email, username, nickname, password);
    }
}


