package com.dongyang.seoyunjeong20230852.service;

import com.dongyang.seoyunjeong20230852.dto.ScheduleRequestDto;
import com.dongyang.seoyunjeong20230852.dto.ScheduleResponseDto;
import com.dongyang.seoyunjeong20230852.entity.Schedule;
import com.dongyang.seoyunjeong20230852.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // 일정 전체 조회
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }

    // 일정 추가
    public void createSchedule(ScheduleRequestDto dto) {
        Schedule schedule = Schedule.builder()
                .title(dto.getTitle())
                .start(LocalDate.parse(dto.getStart()))
                .end((dto.getEnd() == null || dto.getEnd().isBlank()) ? null : LocalDate.parse(dto.getEnd()))
                .build();

        scheduleRepository.save(schedule);
    }

    // 엔티티 → 응답 DTO
    private ScheduleResponseDto toResponseDto(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getTitle(),
                schedule.getStart().toString(),
                schedule.getEnd() != null ? schedule.getEnd().toString() : null
        );
    }
}