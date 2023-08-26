package com.bootpractice.board.dto;

import com.bootpractice.board.domain.Board;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime registDate;
    private LocalDateTime updateDate;
    private String email;
    private String username;


    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.registDate = board.getRegistDate();
        this.updateDate = board.getUpdateDate();
        this.email = board.getMember().getEmail();
        this.username = board.getMember().getUsername();
    }

}
