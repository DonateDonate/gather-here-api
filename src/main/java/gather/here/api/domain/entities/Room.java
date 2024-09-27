package gather.here.api.domain.entities;

import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static gather.here.api.global.util.DateUtil.*;

@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room")
@Entity
public class Room extends BaseTime {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "room_seq")
    @Comment("room 식별값")
    private Long seq;

    @Comment("목적지 위도")
    private Double destinationLat;

    @Comment("목적지 경도")
    private Double destinationLng;

    @Comment("목적지 이름")
    private String destinationName;

    @Comment("상태 진행중 : 1 종료 : 9")
    private int status;

    @Comment("만나는 날짜")
    private LocalDateTime encounterDate;

    @Comment("초대 코드")
    private String shareCode;

    @OneToMany(mappedBy = "room")
    private List<Member> memberList = new ArrayList<>();

    @Builder
    private Room(Double destinationLat, Double destinationLng, String destinationName,int status, LocalDateTime encounterDate, String shareCode) {
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.destinationName = destinationName;
        this.status = status;
        this.encounterDate = encounterDate;
        this.shareCode = shareCode;
    }

    public static Room create(Double destinationLat, Double destinationLng, String destinationName,String encounterDate, Member member){
        LocalDateTime convertedToLocalDateTime = convertToLocalDateTime(encounterDate);

        if(convertedToLocalDateTime == null){
            throw new RoomException(ResponseStatus.INCORRECT_ENCOUNTER_DATE, HttpStatus.CONFLICT);
        }
        if(isPastSeoulTime(convertedToLocalDateTime)){
            throw new RoomException(ResponseStatus.PAST_ENCOUNTER_DATE, HttpStatus.CONFLICT);
        }

        if(isMoreThan24HoursFromNow(convertedToLocalDateTime)){
            throw new RoomException(ResponseStatus.IS_MORE_THAN_24_HOURS_ENCOUNTER_DATE, HttpStatus.CONFLICT);
        }
        Room room = Room.builder()
                .destinationLat(destinationLat)
                .destinationLng(destinationLng)
                .status(1)
                .encounterDate(convertedToLocalDateTime)
                .destinationName(destinationName)
                .shareCode(makeShareCode())
                .build();

        room.memberList.add(member);

        return room;
    }

    public void closeRoom(){
        this.status = 9;
    }

    private static String makeShareCode(){
        return String.valueOf(UUID.randomUUID()).substring(0,4);
    }

    public void addMemberList(Member member){
        this.memberList.add(member);
    }
}
