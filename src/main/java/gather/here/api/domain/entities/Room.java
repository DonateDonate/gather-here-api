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

import static gather.here.api.global.util.DateUtil.convertToLocalDateTime;
import static gather.here.api.global.util.DateUtil.isPastSeoulTime;

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
    private Float destinationLat;

    @Comment("목적지 경도")
    private Float destinationLng;

    @Comment("목적지 이름")
    private String destinationName;

    @Comment("상태 진행중 : 1 종료 : 9")
    private int status;

    @Comment("만나는 날짜")
    private LocalDateTime encounterDate;

    @Comment("초대 코드")
    private String shareCode;

    @Comment("금메달 맴버")
    private Long goldMemberSeq;

    @Comment("은메달 맴버")
    private Long silverMemberSeq;

    @Comment("동메달 맴버")
    private Long bronzeMemberSeq;

    @OneToMany(mappedBy = "room")
    private List<Member> memberList = new ArrayList<>();

    @Builder
    private Room(Float destinationLat, Float destinationLng, String destinationName,int status, LocalDateTime encounterDate, String shareCode) {
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.destinationName = destinationName;
        this.status = status;
        this.encounterDate = encounterDate;
        this.shareCode = shareCode;
    }

    public static Room create(Float destinationLat, Float destinationLng, String destinationName,String encounterDate, Member member){
        LocalDateTime convertedToLocalDateTime = convertToLocalDateTime(encounterDate);

        if(convertedToLocalDateTime == null){
            throw new RoomException(ResponseStatus.ENCOUNTER_DATE_INVALID, HttpStatus.CONFLICT);
        }

        if(isPastSeoulTime(convertedToLocalDateTime)){
            throw new RoomException(ResponseStatus.PAST_DATE_INVALID, HttpStatus.CONFLICT);
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

    private static String makeShareCode(){
        return String.valueOf(UUID.randomUUID()).substring(0,4);
    }

    public void addMemberList(Member member){
        this.memberList.add(member);
    }

}
