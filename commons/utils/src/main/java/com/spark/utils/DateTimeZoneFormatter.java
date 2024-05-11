package com.spark.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeZoneFormatter {

    private DateTimeZoneFormatter() {
    }

    public static ZonedDateTime convertUtcToUserZonedDateTime(long utcTimestamp, String userZoneId) {
        Instant instant = Instant.ofEpochMilli(utcTimestamp);
        return ZonedDateTime.ofInstant(instant, ZoneId.of(userZoneId));
    }
}
