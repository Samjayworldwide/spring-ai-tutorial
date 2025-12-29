package com.samjay.spring_ai_demo.toolcalling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class SimpleDateTimeTool {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {

        return LocalDateTime
                .now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    @Tool(description = "Set an alarm for a specific time")
    public void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {

        var alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);

        log.info("Current time is {}", alarmTime);
    }
}
