package com.anhtu.ftaskbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FTaskBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FTaskBackendApplication.class, args);
    }

}
