package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import lombok.Builder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Builder
public class HealthCheckController {

    @GetMapping("/health-check")
    public ApiResponse<Map<String, String>> healthCheck() {
        return ApiResponse.<Map<String, String>>builder()
                .code(200)
                .message("Health check successful")
                .result(Map.of(
                        "status", "UP",
                        "message", "Service is running normally"
                ))
                .build();
    }
}
