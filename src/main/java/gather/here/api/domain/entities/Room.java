package gather.here.api.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private Room(Float destinationLat, Float destinationLng, int status, LocalDateTime encounterDate, String shareCode,List<Member> memberList) {
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.status = status;
        this.encounterDate = encounterDate;
        this.shareCode = shareCode;
        this.memberList = memberList;
    }

    public Room create(Float destinationLat, Float destinationLng, int status, LocalDateTime encounterDate, Member member){
        Room room = Room.builder()
                .destinationLat(destinationLat)
                .destinationLng(destinationLng)
                .status(status)
                .encounterDate(encounterDate)
                .shareCode(makeShareCode())
                .build();

        room.memberList.add(member);

        return room;
    }

    private static String makeShareCode(){
        return String.valueOf(UUID.randomUUID()).substring(0,8);
    }

}
