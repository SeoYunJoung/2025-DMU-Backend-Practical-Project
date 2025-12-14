package com.dongyang.seoyunjeong20230852.repository;

import com.dongyang.seoyunjeong20230852.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 최상위 댓글만 조회 (parent가 null인 경우)
    List<Comment> findAllByParentIsNullOrderByCreatedAtDesc();

    // 특정 댓글의 대댓글만 조회
    List<Comment> findAllByParentIdOrderByCreatedAtAsc(Long parentId);
}