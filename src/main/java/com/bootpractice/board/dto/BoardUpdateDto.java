package com.bootpractice.board.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BoardUpdateDto {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "content is required")
    private String content;
}
