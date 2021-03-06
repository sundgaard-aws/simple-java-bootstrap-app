package com.opusmagus;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.TimeZone;

@Configuration
public class AppConfig {
    @Bean
    public Gson Json() {
        return new Gson();
    }

    @Bean
    public Calendar Calendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("CET"));
    }

    @Bean
    public AWSLogs CloudWatchLogger() { return AWSLogsClientBuilder.defaultClient(); }
}


