package com.masterproject.Master.Bob.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeConverter {
    public static Timestamp convertToTimestamp(String dateTimeString) {
        // Parse the date-time string into a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);

        // Convert LocalDateTime to a timestamp
        return Timestamp.valueOf(dateTime);
    }
}
