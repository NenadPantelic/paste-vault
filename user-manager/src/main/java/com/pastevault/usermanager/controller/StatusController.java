package com.pastevault.usermanager.controller;

import com.pastevault.usermanager.dto.response.StatusReport;
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
        return new StatusReport(
                String.format(
                        "User manager service has been up and running for %d milliseconds",
                        System.currentTimeMillis() - START_TIME
                )
        );
    }
}
