package com.bootpractice.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private LocalDateTime registDate;
    private LocalDateTime updateDate;

}
