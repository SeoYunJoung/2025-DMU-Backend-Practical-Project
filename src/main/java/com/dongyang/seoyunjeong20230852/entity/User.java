package com.dongyang.seoyunjeong20230852.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    //회원가입시 반드시 유저로 고정
    // ** 관리자 작성 SQL문 **
    //insert into users(email, name, password,role)  values ('admin@naver.com','관리자','$2a$10$YUdUC.h1hAhJ53n0S61Fseky82/xdtBZz.GyDPmliLfbG6hF7sJMe','ROLE_ADMIN');
    @Enumerated(EnumType.STRING)
    private Role role;

    // ✅ 댓글과의 연관관계 설정 추가
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
