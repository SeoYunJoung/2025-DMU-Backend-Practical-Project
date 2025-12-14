package com.dongyang.seoyunjeong20230852.dto;

import com.dongyang.seoyunjeong20230852.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDto {
    private String title;
    private String start;  // "yyyy-MM-dd"
    private String end;

    public ScheduleResponseDto(Schedule schedule) {
        this.title = schedule.getTitle();
        this.start = schedule.getStart().toString();
        this.end = schedule.getEnd() != null
                ? schedule.getEnd().plusDays(1).toString()
                : null;  // ← FullCalendar에서 보이기 위한 +1
    }
}
