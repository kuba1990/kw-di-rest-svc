package com.bi.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MicroserviceConfig.class)
public class DiRestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiRestServiceApplication.class, args);
    }
}
