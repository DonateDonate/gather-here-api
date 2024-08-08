package gather.here.api.global.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static gather.here.api.global.util.DateUtil.*;

class DateUtilTest {

    @Test
    public void isPastSeoulTimeTest(){
        Date date = new Date();
        LocalDateTime nowDate = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        LocalDateTime nowPlusOneDay = nowDate.plusDays(1L);
        LocalDateTime nowMinusOneDay = nowDate.minusDays(1L);

        boolean plusDay = isPastSeoulTime(nowPlusOneDay);
        boolean minusDay = isPastSeoulTime(nowMinusOneDay);
        Assertions.assertThat(plusDay).isFalse();
        Assertions.assertThat(minusDay).isTrue();
    }













}