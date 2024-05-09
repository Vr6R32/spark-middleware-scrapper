package com.spark.data.currency_pair_rate_history;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class DateTimeZoneFormatter {

    private DateTimeZoneFormatter() {
    }

    public static ZonedDateTime convertUtcToZonedDateTime(long utcTimestamp, String userZoneId) {
        Instant instant = Instant.ofEpochMilli(utcTimestamp);
        return ZonedDateTime.ofInstant(instant, ZoneId.of(userZoneId));
    }
}
