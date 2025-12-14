package com.dongyang.seoyunjeong20230852.dto;

import com.dongyang.seoyunjeong20230852.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto { //회원가입 및 유저 리퀘스트
    private String email;
    private String password;
    private String name;
    private Role role;
}
