package com.masterproject.Master.Bob.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public Timestamp convertToTimestamp(String dateTimeString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        return Timestamp.valueOf(localDateTime);
    }
}
