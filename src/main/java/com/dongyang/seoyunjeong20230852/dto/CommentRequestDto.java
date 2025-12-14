package com.dongyang.seoyunjeong20230852.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private String content;
    private Long parentId; //null이면 최상의 댓글
}