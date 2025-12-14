package com.dongyang.seoyunjeong20230852.dto;

import com.dongyang.seoyunjeong20230852.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentResponseDto {

    private Long id;
    private String userName;
    private String content;
    private String createdAt;
    private List<CommentResponseDto> replies;

    //엔티티 -> DTO 변환
    public static CommentResponseDto fromEntityWithReplies(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userName(comment.getUser().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .replies(
                        comment.getReplies().stream()
                                .map(reply -> CommentResponseDto.builder()
                                        .id(reply.getId())
                                        .userName(reply.getUser().getName())
                                        .content(reply.getContent())
                                        .createdAt(reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                        .replies(List.of()) //대댓글은 댓글이 없도록 구현
                                        .build()
                                ).collect(Collectors.toList())
                )
                .build();
    }
}
