package gather.here.api.domain.entities;

import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static gather.here.api.global.util.DateUtil.convertLocalDateTimeToString;

class RoomTest {

    @DisplayName("sut는 room이 status 1로 생성된다")
    @Test
    public void statusTest(){
        //arrange
        Float destinationLat = 1F;
        Float destinationLng = 2F;
        String destinationName = "사나몬 집";
        Room sut = null;

        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusDays(10);;

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        Member member = Member.create("01012345678","12341234","ENCODEPASSWORDENCODEPASSWORDENC");

        //act
        sut = Room.create(destinationLat,destinationLng,destinationName,encounterDate,member);

        //assert
        Assertions.assertThat(sut.getStatus()).isEqualTo(1);
        Assertions.assertThat(sut.getStatus()).isNotNull();
    }

    @DisplayName("sut는 room shareCode 값이 null이 아니다")
    @Test
    public void shareCodeNullTest(){

        //arrange
        Float destinationLat = 1F;
        Float destinationLng = 2F;
        String destinationName = "사나몬 집";
        Room sut = null;

        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusDays(10);;

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        Member member = Member.create("01012345678","12341234","ENCODEPASSWORDENCODEPASSWORDENC");

        //act
        sut = Room.create(destinationLat,destinationLng,destinationName,encounterDate,member);

        //assert
        Assertions.assertThat(sut.getShareCode()).isNotNull();

    }

    @DisplayName("sut는 encounterDate 필드가 yyyy-mm-dd HH:mm에 맞지 않으면 예외가 발생한다")
    @Test
    public void encounterDateFormatTest(){
        //arrange
        Float destinationLat = 1F;
        Float destinationLng = 2F;
        String destinationName = "사나몬 집";
        Room sut = null;
        RoomException actual = null;
        String encounterDate = "20240202020202";
        Member member = Member.create("01012345678","12341234","ENCODEPASSWORDENCODEPASSWORDENC");

        //act
        try {
            sut = Room.create(destinationLat, destinationLng, destinationName, encounterDate, member);
        }catch (RoomException e){
            actual=e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getResponseStatus()).isEqualTo(ResponseStatus.ENCOUNTER_DATE_INVALID);
        Assertions.assertThat(actual).isInstanceOf(RoomException.class);
    }

    @DisplayName("sut는 encounterDate 필드가 과거이면 예외가 발생한다")
    @Test
    public void isEncounterDateInPastTest(){

        //arrange
        Float destinationLat = 1F;
        Float destinationLng = 2F;
        String destinationName = "사나몬 집";
        Room sut = null;
        RoomException actual = null;

        Date date = new Date();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .minusDays(10);;

        String encounterDate = convertLocalDateTimeToString(localDateTime);
        Member member = Member.create("01012345678","12341234","ENCODEPASSWORDENCODEPASSWORDENC");

        //act
        try {
            sut = Room.create(destinationLat, destinationLng, destinationName, encounterDate, member);
        }catch (RoomException e){
            actual=e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getResponseStatus()).isEqualTo(ResponseStatus.PAST_DATE_INVALID);
        Assertions.assertThat(actual).isInstanceOf(RoomException.class);

    }

}