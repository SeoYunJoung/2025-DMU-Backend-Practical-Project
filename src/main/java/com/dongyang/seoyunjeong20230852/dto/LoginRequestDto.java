package com.dongyang.seoyunjeong20230852.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto { //로그인 리퀘스트
    private String email;
    private String password;
}