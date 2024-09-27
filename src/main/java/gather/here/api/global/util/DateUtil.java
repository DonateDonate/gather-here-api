package gather.here.api.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static boolean isMoreThan24HoursFromNow(LocalDateTime inputLocalDate) {
        LocalDateTime nowDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // 24시간을 더한 현재 시간
        LocalDateTime twentyFourHoursFromNow = nowDate.plusHours(24);
        return inputLocalDate.isAfter(twentyFourHoursFromNow);
    }

    public static String convertLocalDateTimeToString(LocalDateTime localDatetime) {
        return localDatetime.format(FORMATTER);
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        try {
            // LocalDateTime으로 파싱한 후, 한국 시간대에 맞게 변환
            return LocalDateTime.parse(dateTimeString, FORMATTER);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
