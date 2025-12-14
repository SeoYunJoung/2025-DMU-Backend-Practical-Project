package com.dongyang.seoyunjeong20230852.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {
    private String title;
    private String start;  // "yyyy-MM-dd" 형식의 문자열
    private String end;    // optional
}
