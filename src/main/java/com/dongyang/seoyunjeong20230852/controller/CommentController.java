package com.dongyang.seoyunjeong20230852.controller;

import com.dongyang.seoyunjeong20230852.dto.CommentRequestDto;
import com.dongyang.seoyunjeong20230852.dto.CommentResponseDto;
import com.dongyang.seoyunjeong20230852.dto.ScheduleRequestDto;
import com.dongyang.seoyunjeong20230852.security.CustomUserDetails;
import com.dongyang.seoyunjeong20230852.service.CommentService;
import com.dongyang.seoyunjeong20230852.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final ScheduleService scheduleService;

    //댓글 등록
    @PostMapping
    public ResponseEntity<Void> postComment(@RequestBody CommentRequestDto dto,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.addComment(dto.getContent(), userDetails.getUsername(), dto.getParentId());
        return ResponseEntity.ok().build();
    }

    //댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getAllComments() {
        List<CommentResponseDto> response = commentService.getAllTopLevelComments().stream()
                .map(CommentResponseDto::fromEntityWithReplies)
                .toList();
        return ResponseEntity.ok(response);
    }
}
