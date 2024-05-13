package com.spark.data.domain.currency_pair_rate_history;

public class TimeWindowConverter {

    // TODO ITS CONVERTER FOR FURTHER DYNAMIC ENDPOINT WHICH WILL RETURN RESULTS BASED ON CLIENT PROVIDED TIME WINDOW
    //   it might be a good idea to make an endpoint that can give flexible cryptocurrency rate history
    //   based on provided time window, instead of just having a fixed endpoint like /lastday.
    //   of course, the specific approach will be determined by the business requirements however,
    //   creating a flexible controller is crucial for future scalability and adaptability. ideally,
    //   we could support both methods of data retrieval to maximize utility


    public static final int ONE_HOUR_IN_SECONDS = 3600;
    public static final int ONE_DAY_IN_SECONDS = 86400;
    public static final int THREE_DAYS_IN_SECONDS = 259200;
    public static final int SEVEN_DAYS_IN_SECONDS = 604800;
    public static final int ONE_MONTH_IN_SECONDS = 2592000;
    public static final int THREE_MONTHS_IN_SECONDS = 7776000;
    public static final int SIX_MONTHS_IN_SECONDS = 15552000;
    public static final int ONE_YEAR_IN_SECONDS = 31536000;

    private TimeWindowConverter() {
    }

    /**
     * Converts a number of seconds into a standardized, human-readable time window format.
     * This method is particularly useful for presenting durations in a more intuitive format, such as "1h" for one hour
     * or "1y" for one year, making it easier for users to understand time durations at a glance.
     *
     * @param seconds the time duration in seconds. This should be one of the predefined time constants such as
     *                ONE_HOUR_IN_SECONDS, ONE_DAY_IN_SECONDS, etc.
     * @return a string representing the time window in a human-readable format. Formats include:
     *         "1h" for one hour,
     *         "24h" for one day (24 hours),
     *         "3d" for three days,
     *         "7d" for seven days,
     *         "1m" for one month (approximately 30 days),
     *         "3m" for three months (approximately 90 days),
     *         "6m" for six months (approximately 180 days),
     *         "1y" for one year (approximately 365 days).
     * @throws IllegalArgumentException if the input seconds do not correspond to any predefined time window,
     *         indicating that the user should provide a number of seconds that matches one of the supported predefined constants.
     */

    public static String convertSecondsToTimeWindowFormat(int seconds) {
        return switch (seconds) {
            case ONE_HOUR_IN_SECONDS -> "1h";
            case ONE_DAY_IN_SECONDS -> "24h";
            case THREE_DAYS_IN_SECONDS -> "3d";
            case SEVEN_DAYS_IN_SECONDS -> "7d";
            case ONE_MONTH_IN_SECONDS -> "1m";
            case THREE_MONTHS_IN_SECONDS -> "3m";
            case SIX_MONTHS_IN_SECONDS -> "6m";
            case ONE_YEAR_IN_SECONDS -> "1y";
            default -> throw new IllegalArgumentException("Unsupported time window format: " + seconds);
        };
    }

    /**
     * Converts a human-readable time window format (e.g., "1h", "24h", "1m", "1y") into the corresponding number of seconds.
     * This method facilitates the conversion of simplified time descriptors into a more precise format used in programming,
     * which is the number of seconds.
     *
     * @param timeWindow the string representing the time window. Accepted formats include:
     *                   "1h" for one hour,
     *                   "24h" for one day (24 hours),
     *                   "3d" for three days,
     *                   "7d" for seven days,
     *                   "1m" for one month (approximately 30 days),
     *                   "3m" for three months (approximately 90 days),
     *                   "6m" for six months (approximately 180 days),
     *                   "1y" for one year (approximately 365 days).
     * @return the equivalent number of seconds for the given time window format.
     * @throws IllegalArgumentException if the provided time window format is not supported, indicating that the user
     *         should supply one of the predefined and supported time window formats.
     */

    public static int convertTimeWindowFormatToSeconds(String timeWindow) {
        return switch (timeWindow) {
            case "1h" -> ONE_HOUR_IN_SECONDS;
            case "24h" -> ONE_DAY_IN_SECONDS;
            case "3d" -> THREE_DAYS_IN_SECONDS;
            case "7d" -> SEVEN_DAYS_IN_SECONDS;
            case "1m" -> ONE_MONTH_IN_SECONDS;
            case "3m" -> THREE_MONTHS_IN_SECONDS;
            case "6m" -> SIX_MONTHS_IN_SECONDS;
            case "1y" -> ONE_YEAR_IN_SECONDS;
            default -> throw new IllegalArgumentException("Unsupported time window format: " + timeWindow);
        };
    }
}