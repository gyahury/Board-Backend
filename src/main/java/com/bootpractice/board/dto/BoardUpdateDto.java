package com.bootpractice.board.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BoardUpdateDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
//  @Size(min = 10, message = "내용은 10자 이상이어야 합니다.")
    private String content;
}
