package com.dongyang.seoyunjeong20230852.service;

import com.dongyang.seoyunjeong20230852.entity.Comment;
import com.dongyang.seoyunjeong20230852.entity.User;
import com.dongyang.seoyunjeong20230852.repository.CommentRepository;
import com.dongyang.seoyunjeong20230852.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    //댓글 저장
    public void addComment(String content, String email, Long parentId) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        Comment.CommentBuilder builder = Comment.builder()
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now());

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElseThrow(() ->
                    new IllegalArgumentException("댓글을 찾을 수 없습니다."));
            builder.parent(parent);
        }

        commentRepository.save(builder.build());
    }

    //최상위 댓글 전체 조회
    public List<Comment> getAllTopLevelComments() {
        return commentRepository.findAllByParentIsNullOrderByCreatedAtDesc();
    }

    //특정 댓글의 대댓글 조회
    public List<Comment> getReplies(Long parentId) {
        return commentRepository.findAllByParentIdOrderByCreatedAtAsc(parentId);
    }
}