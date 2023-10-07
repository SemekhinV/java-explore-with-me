package ru.practicum.stats.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class StatsServerApp {

    public static void main(String[] args) {
        SpringApplication.run(StatsServerApp.class, args);
    }

}