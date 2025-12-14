package com.dongyang.seoyunjeong20230852.controller;

import com.dongyang.seoyunjeong20230852.dto.ScheduleRequestDto;
import com.dongyang.seoyunjeong20230852.dto.ScheduleResponseDto;
import com.dongyang.seoyunjeong20230852.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createSchedule(@RequestBody ScheduleRequestDto dto) {
        scheduleService.createSchedule(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }
}