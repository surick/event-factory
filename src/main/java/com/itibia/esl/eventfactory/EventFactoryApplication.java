package com.itibia.esl.eventfactory;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventFactoryApplication {
    public final static String EVENT = "event";

    @Bean
    public Queue eventQueue() {
        return new Queue(EventFactoryApplication.EVENT);
    }

    public static void main(String[] args) {
        SpringApplication.run(EventFactoryApplication.class, args);
    }

}
