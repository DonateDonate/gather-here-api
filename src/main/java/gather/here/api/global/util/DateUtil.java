package gather.here.api.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isPastSeoulTime(LocalDateTime inputLocalDate){
        Date date = new Date();
        LocalDateTime nowDate = date.toInstant()
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();
        return inputLocalDate.isBefore(nowDate);
    }

    public static String convertLocalDateTimeToString(LocalDateTime localDatetime) {
        return localDatetime.format(FORMATTER);
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeString, FORMATTER.withZone(ZoneId.of("Asia/Seoul")));
            return zonedDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
