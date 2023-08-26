package com.bootpractice.board.dto;

import com.bootpractice.board.domain.Board;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BoardCreateDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    public Board toEntity() {
        return new Board(title, content);
    }
}
