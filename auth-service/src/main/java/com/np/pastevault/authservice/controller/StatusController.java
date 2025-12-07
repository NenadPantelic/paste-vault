package com.np.pastevault.authservice.controller;

import com.pastevault.common.dto.response.StatusReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/status")
public class StatusController {

    private static final long START_TIME = System.currentTimeMillis();

    @GetMapping
    public StatusReport getStatus() {
        return new StatusReport(System.currentTimeMillis() - START_TIME);
    }
}
